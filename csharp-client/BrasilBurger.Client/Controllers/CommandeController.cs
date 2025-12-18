using Microsoft.AspNetCore.Mvc;
using BrasilBurger.ClientApp.Data;
using BrasilBurger.ClientApp.Data.Repositories;
using BrasilBurger.ClientApp.Models;
using BrasilBurger.ClientApp.Helpers;

namespace BrasilBurger.ClientApp.Controllers
{
    public class CommandeController : Controller
    {
        private readonly CommandeRepository _commandeRepository;
        private readonly ApplicationDbContext _context;
        private readonly IConfiguration _configuration;

        public CommandeController(ApplicationDbContext context, IConfiguration configuration)
        {
            _commandeRepository = new CommandeRepository(context);
            _context = context;
            _configuration = configuration;
        }

        // GET: /Commande/Checkout
        public IActionResult Checkout()
        {
            try
            {
                Console.WriteLine("=== CHECKPOINT: Checkout GET Start ===");
                
                // Vérifier si connecté
                if (HttpContext.Session.GetInt32("ClientId") == null)
                {
                    Console.WriteLine("ERROR: Client non connecté");
                    TempData["Error"] = "Connectez-vous pour commander.";
                    return RedirectToAction("Login", "Auth");
                }

                // Vérifier si panier non vide
                var panier = HttpContext.Session.GetPanier();
                Console.WriteLine($"CHECKPOINT: Panier contient {panier.Count} items");
                
                if (!panier.Any())
                {
                    Console.WriteLine("ERROR: Panier vide");
                    TempData["Error"] = "Votre panier est vide.";
                    return RedirectToAction("Index", "Panier");
                }

                ViewBag.Total = HttpContext.Session.TotalPanier();
                ViewBag.Panier = panier;
                
                Console.WriteLine($"SUCCESS: Checkout page ready, Total = {ViewBag.Total}");
                return View();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"FATAL ERROR in Checkout: {ex.Message}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                TempData["Error"] = $"Erreur: {ex.Message}";
                return RedirectToAction("Index", "Home");
            }
        }

        // POST: /Commande/Create
        [HttpPost]
        public async Task<IActionResult> Create(string typeRecuperation, string? adresse)
        {
            try
            {
                Console.WriteLine("=== CHECKPOINT 1: Create POST Start ===");
                Console.WriteLine($"Type: {typeRecuperation}, Adresse: {adresse}");

                // Vérifier authentification
                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (clientId == null)
                {
                    Console.WriteLine("ERROR: Client non connecté");
                    return RedirectToAction("Login", "Auth");
                }
                Console.WriteLine($"CHECKPOINT 2: ClientId = {clientId}");

                // Récupérer panier
                var panier = HttpContext.Session.GetPanier();
                if (!panier.Any())
                {
                    Console.WriteLine("ERROR: Panier vide");
                    TempData["Error"] = "Panier vide.";
                    return RedirectToAction("Index", "Panier");
                }
                Console.WriteLine($"CHECKPOINT 3: Panier OK, {panier.Count} items");

                // Validation type récupération
                if (string.IsNullOrWhiteSpace(typeRecuperation) || 
                    !new[] { "sur_place", "emporter", "livraison" }.Contains(typeRecuperation.ToLower()))
                {
                    Console.WriteLine($"ERROR: Type récupération invalide: {typeRecuperation}");
                    TempData["Error"] = "Type de récupération invalide.";
                    return RedirectToAction("Checkout");
                }
                Console.WriteLine("CHECKPOINT 4: Type récupération validé");

                // Si livraison, adresse obligatoire
                if (typeRecuperation.ToLower() == "livraison" && string.IsNullOrWhiteSpace(adresse))
                {
                    Console.WriteLine("ERROR: Adresse livraison manquante");
                    TempData["Error"] = "Adresse de livraison requise.";
                    return RedirectToAction("Checkout");
                }
                Console.WriteLine("CHECKPOINT 5: Validation adresse OK");

                // Calculer montant total
                var montantTotal = panier.Sum(p => p.Total);
                Console.WriteLine($"CHECKPOINT 6: Montant total = {montantTotal}");

                // === MÉTHODE DIRECTE SQL (Bypass Entity Framework) ===
                Console.WriteLine("CHECKPOINT 7: Début insertion SQL directe");
                
                var connectionString = _configuration.GetConnectionString("DefaultConnection");
                using var connection = new Npgsql.NpgsqlConnection(connectionString);
                await connection.OpenAsync();
                Console.WriteLine("CHECKPOINT 8: Connexion BD ouverte");

                // Insérer commande
                var cmdSql = @"
                    INSERT INTO commande (client_id, montant_total, type_recuperation, etat, adresse_livraison)
                    VALUES (@clientId, @montantTotal, @typeRecuperation, @etat, @adresse)
                    RETURNING id";

                using var cmd = new Npgsql.NpgsqlCommand(cmdSql, connection);
                cmd.Parameters.AddWithValue("clientId", clientId.Value);
                cmd.Parameters.AddWithValue("montantTotal", montantTotal);
                cmd.Parameters.AddWithValue("typeRecuperation", typeRecuperation.ToLower());
                cmd.Parameters.AddWithValue("etat", "en_cours");
                cmd.Parameters.AddWithValue("adresse", (object?)adresse ?? DBNull.Value);

                Console.WriteLine("CHECKPOINT 9: Exécution INSERT commande...");
                var commandeId = (int)(await cmd.ExecuteScalarAsync())!;
                Console.WriteLine($"SUCCESS: Commande créée, ID = {commandeId}");

                // Insérer lignes
                Console.WriteLine($"CHECKPOINT 10: Insertion {panier.Count} lignes...");
                foreach (var item in panier)
                {
                    var ligneSql = @"
                        INSERT INTO lignecommande (commande_id, produit_type, produit_id, quantite, prix_unitaire)
                        VALUES (@commandeId, @produitType, @produitId, @quantite, @prixUnitaire)";

                    using var ligneCmd = new Npgsql.NpgsqlCommand(ligneSql, connection);
                    ligneCmd.Parameters.AddWithValue("commandeId", commandeId);
                    ligneCmd.Parameters.AddWithValue("produitType", item.Type);
                    ligneCmd.Parameters.AddWithValue("produitId", item.ProduitId);
                    ligneCmd.Parameters.AddWithValue("quantite", item.Quantite);
                    ligneCmd.Parameters.AddWithValue("prixUnitaire", item.Prix);

                    await ligneCmd.ExecuteNonQueryAsync();
                    Console.WriteLine($"  - Ligne insérée: {item.Nom} x{item.Quantite}");
                }

                Console.WriteLine("CHECKPOINT 11: Toutes les lignes insérées");

                // Vider le panier
                HttpContext.Session.ViderPanier();
                Console.WriteLine("CHECKPOINT 12: Panier vidé");

                TempData["Success"] = "Commande créée avec succès !";
                Console.WriteLine($"=== SUCCESS: Redirection vers Confirmation/{commandeId} ===");
                
                return RedirectToAction("Confirmation", new { id = commandeId });
            }
            catch (Npgsql.NpgsqlException ex)
            {
                Console.WriteLine("=== ERREUR POSTGRESQL ===");
                Console.WriteLine($"Message: {ex.Message}");
                Console.WriteLine($"Code: {ex.SqlState}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                
                TempData["Error"] = $"Erreur base de données: {ex.Message}";
                return RedirectToAction("Checkout");
            }
            catch (Exception ex)
            {
                Console.WriteLine("=== ERREUR GÉNÉRALE ===");
                Console.WriteLine($"Message: {ex.Message}");
                Console.WriteLine($"Type: {ex.GetType().Name}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                
                TempData["Error"] = $"Erreur: {ex.Message}";
                return RedirectToAction("Checkout");
            }
        }

        // GET: /Commande/Confirmation/5
        public async Task<IActionResult> Confirmation(int id)
        {
            try
            {
                Console.WriteLine($"=== CHECKPOINT: Confirmation pour commande {id} ===");
                
                var commande = await _commandeRepository.GetByIdAsync(id);
                
                if (commande == null)
                {
                    Console.WriteLine($"ERROR: Commande {id} introuvable");
                    return NotFound();
                }

                // Vérifier que c'est bien la commande du client connecté
                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (commande.ClientId != clientId)
                {
                    Console.WriteLine($"ERROR: Commande {id} n'appartient pas au client {clientId}");
                    return Forbid();
                }

                Console.WriteLine($"SUCCESS: Affichage confirmation commande {id}");
                return View(commande);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERROR in Confirmation: {ex.Message}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                TempData["Error"] = $"Erreur: {ex.Message}";
                return RedirectToAction("Index", "Home");
            }
        }

        // GET: /Commande/MesCommandes
        public async Task<IActionResult> MesCommandes()
        {
            try
            {
                Console.WriteLine("=== CHECKPOINT: MesCommandes ===");
                
                // Vérifier authentification
                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (clientId == null)
                {
                    Console.WriteLine("ERROR: Client non connecté");
                    return RedirectToAction("Login", "Auth");
                }

                var commandes = await _commandeRepository.GetByClientIdAsync(clientId.Value);
                Console.WriteLine($"SUCCESS: {commandes.Count} commandes trouvées");
                
                return View(commandes);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERROR in MesCommandes: {ex.Message}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                TempData["Error"] = $"Erreur: {ex.Message}";
                return RedirectToAction("Index", "Home");
            }
        }
    }
}