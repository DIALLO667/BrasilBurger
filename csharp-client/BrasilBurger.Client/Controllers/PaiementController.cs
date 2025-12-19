using Microsoft.AspNetCore.Mvc;
using BrasilBurger.ClientApp.Data;
using BrasilBurger.ClientApp.Data.Repositories;
using Microsoft.EntityFrameworkCore;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Controllers
{
    public class PaiementController : Controller
    {
        private readonly CommandeRepository _commandeRepository;
        private readonly ApplicationDbContext _context;
        private readonly IConfiguration _configuration;

        public PaiementController(ApplicationDbContext context, IConfiguration configuration)
        {
            _commandeRepository = new CommandeRepository(context);
            _context = context;
            _configuration = configuration;
        }

        // GET: /Paiement/Index?commandeId=5
        public async Task<IActionResult> Index(int commandeId)
        {
            try
            {
                Console.WriteLine($"=== PAIEMENT: Index pour commande {commandeId} ===");

                // Vérifier authentification
                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (clientId == null)
                {
                    Console.WriteLine("ERROR: Client non connecté");
                    return RedirectToAction("Login", "Auth");
                }

                // Récupérer la commande
                var commande = await _commandeRepository.GetByIdAsync(commandeId);
                if (commande == null)
                {
                    Console.WriteLine($"ERROR: Commande {commandeId} introuvable");
                    return NotFound();
                }

                // Vérifier que c'est la commande du client connecté
                if (commande.ClientId != clientId)
                {
                    Console.WriteLine($"ERROR: Commande {commandeId} n'appartient pas au client");
                    return Forbid();
                }

                // Vérifier si déjà payée
                var paiementExistant = await _context.Set<Paiement>()
                    .FirstOrDefaultAsync(p => p.CommandeId == commandeId);

                if (paiementExistant != null)
                {
                    Console.WriteLine($"INFO: Commande {commandeId} déjà payée");
                    TempData["Info"] = "Cette commande a déjà été payée.";
                    return RedirectToAction("Confirmation", "Commande", new { id = commandeId });
                }

                Console.WriteLine($"SUCCESS: Affichage page paiement pour {commande.MontantTotal} FCFA");
                return View(commande);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERROR in Paiement.Index: {ex.Message}");
                TempData["Error"] = $"Erreur: {ex.Message}";
                return RedirectToAction("Index", "Home");
            }
        }

        // POST: /Paiement/Traiter
        [HttpPost]
        public async Task<IActionResult> Traiter(int commandeId, string methode, string? numeroTelephone)
        {
            try
            {
                Console.WriteLine($"=== PAIEMENT: Traiter commande {commandeId}, méthode {methode} ===");

                // Vérifier authentification
                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (clientId == null)
                {
                    Console.WriteLine("ERROR: Client non connecté");
                    return RedirectToAction("Login", "Auth");
                }

                // Validation méthode
                if (string.IsNullOrWhiteSpace(methode) || 
                    !new[] { "wave", "om" }.Contains(methode.ToLower()))
                {
                    Console.WriteLine($"ERROR: Méthode invalide: {methode}");
                    TempData["Error"] = "Méthode de paiement invalide.";
                    return RedirectToAction("Index", new { commandeId });
                }

                // Validation numéro téléphone
                if (string.IsNullOrWhiteSpace(numeroTelephone))
                {
                    Console.WriteLine("ERROR: Numéro téléphone manquant");
                    TempData["Error"] = "Numéro de téléphone requis.";
                    return RedirectToAction("Index", new { commandeId });
                }

                // Récupérer la commande
                var commande = await _commandeRepository.GetByIdAsync(commandeId);
                if (commande == null || commande.ClientId != clientId)
                {
                    Console.WriteLine("ERROR: Commande introuvable ou non autorisée");
                    return Forbid();
                }

                // Vérifier si déjà payée
                var paiementExistant = await _context.Set<Paiement>()
                    .FirstOrDefaultAsync(p => p.CommandeId == commandeId);

                if (paiementExistant != null)
                {
                    Console.WriteLine("INFO: Déjà payée");
                    TempData["Info"] = "Cette commande a déjà été payée.";
                    return RedirectToAction("Success", new { commandeId });
                }

                Console.WriteLine("CHECKPOINT: Simulation paiement...");

                // === SIMULATION PAIEMENT ===
                // En production : Appel API Wave/Orange Money
                // Ici : On simule un succès à 90%
                var random = new Random();
                var isSuccess = random.Next(1, 101) <= 90; // 90% succès

                if (!isSuccess)
                {
                    Console.WriteLine("SIMULATION: Paiement échoué");
                    TempData["Error"] = "Le paiement a échoué. Réessayez.";
                    return RedirectToAction("Index", new { commandeId });
                }

                Console.WriteLine("SIMULATION: Paiement réussi");

                // Générer référence transaction
                var reference = $"{methode.ToUpper()}-{DateTime.Now:yyyyMMddHHmmss}-{commandeId}";

                // === INSERTION SQL DIRECTE ===
                var connectionString = _configuration.GetConnectionString("DefaultConnection");
                using var connection = new Npgsql.NpgsqlConnection(connectionString);
                await connection.OpenAsync();

                var sql = @"
                    INSERT INTO paiement (commande_id, montant, methode, reference_transaction)
                    VALUES (@commandeId, @montant, @methode, @reference)";

                using var cmd = new Npgsql.NpgsqlCommand(sql, connection);
                cmd.Parameters.AddWithValue("commandeId", commandeId);
                cmd.Parameters.AddWithValue("montant", commande.MontantTotal);
                cmd.Parameters.AddWithValue("methode", methode.ToLower());
                cmd.Parameters.AddWithValue("reference", reference);

                await cmd.ExecuteNonQueryAsync();
                Console.WriteLine($"SUCCESS: Paiement enregistré, ref: {reference}");

                TempData["Success"] = "Paiement effectué avec succès !";
                return RedirectToAction("Success", new { commandeId });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERROR in Paiement.Traiter: {ex.Message}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                TempData["Error"] = $"Erreur lors du paiement: {ex.Message}";
                return RedirectToAction("Index", new { commandeId });
            }
        }

        // GET: /Paiement/Success?commandeId=5
        public async Task<IActionResult> Success(int commandeId)
        {
            try
            {
                Console.WriteLine($"=== PAIEMENT: Success pour commande {commandeId} ===");

                // Vérifier authentification
                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (clientId == null)
                {
                    return RedirectToAction("Login", "Auth");
                }

                // Récupérer la commande
                var commande = await _commandeRepository.GetByIdAsync(commandeId);
                if (commande == null || commande.ClientId != clientId)
                {
                    return Forbid();
                }

                // Récupérer le paiement
                var paiement = await _context.Set<Paiement>()
                    .FirstOrDefaultAsync(p => p.CommandeId == commandeId);

                if (paiement == null)
                {
                    Console.WriteLine("ERROR: Paiement introuvable");
                    TempData["Error"] = "Paiement introuvable.";
                    return RedirectToAction("Index", new { commandeId });
                }

                ViewBag.Commande = commande;
                Console.WriteLine("SUCCESS: Affichage page confirmation paiement");
                
                return View(paiement);
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERROR in Paiement.Success: {ex.Message}");
                TempData["Error"] = $"Erreur: {ex.Message}";
                return RedirectToAction("Index", "Home");
            }
        }
    }
}