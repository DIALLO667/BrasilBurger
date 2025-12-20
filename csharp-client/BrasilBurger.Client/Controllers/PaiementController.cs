using Microsoft.AspNetCore.Mvc;
using BrasilBurger.ClientApp.Data;
using BrasilBurger.ClientApp.Data.Repositories;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Controllers
{
    public class PaiementController : Controller
    {
        private readonly CommandeRepository _commandeRepository;
        private readonly IConfiguration _configuration;

        public PaiementController(ApplicationDbContext context, IConfiguration configuration)
        {
            _commandeRepository = new CommandeRepository(context);
            _configuration = configuration;
        }

        public async Task<IActionResult> Index(int commandeId)
        {
            try
            {
                Console.WriteLine($"=== PAIEMENT: Index pour commande {commandeId} ===");
                
                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (clientId == null)
                {
                    Console.WriteLine("ERROR: Client non connecté");
                    TempData["Error"] = "Veuillez vous connecter.";
                    return RedirectToAction("Login", "Auth");
                }

                var commande = await _commandeRepository.GetByIdAsync(commandeId);
                
                if (commande == null)
                {
                    Console.WriteLine($"ERROR: Commande {commandeId} introuvable");
                    TempData["Error"] = "Commande introuvable.";
                    return RedirectToAction("MesCommandes", "Commande");
                }

                if (commande.ClientId != clientId)
                {
                    Console.WriteLine($"ERROR: Commande n'appartient pas au client");
                    TempData["Error"] = "Accès non autorisé.";
                    return RedirectToAction("MesCommandes", "Commande");
                }

                // Vérifier paiement via SQL direct (pas EF Core)
                var connectionString = _configuration.GetConnectionString("DefaultConnection");
                using var connection = new Npgsql.NpgsqlConnection(connectionString);
                await connection.OpenAsync();

                var checkSql = "SELECT COUNT(*) FROM paiement WHERE commande_id = @commandeId";
                using var checkCmd = new Npgsql.NpgsqlCommand(checkSql, connection);
                checkCmd.Parameters.AddWithValue("commandeId", commandeId);
                
                var count = Convert.ToInt32(await checkCmd.ExecuteScalarAsync());
                
                if (count > 0)
                {
                    Console.WriteLine($"WARNING: Commande déjà payée");
                    TempData["Info"] = "Cette commande a déjà été payée.";
                    return RedirectToAction("Confirmation", "Commande", new { id = commandeId });
                }

                ViewBag.Commande = commande;
                Console.WriteLine($"SUCCESS: Affichage page paiement");
                
                return View();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"FATAL ERROR in Paiement.Index: {ex.Message}");
                Console.WriteLine($"StackTrace: {ex.StackTrace}");
                TempData["Error"] = "Erreur lors du chargement du paiement.";
                return RedirectToAction("Index", "Home");
            }
        }

        [HttpPost]
        public async Task<IActionResult> Traiter(int commandeId, string methode, string numeroTelephone)
        {
            try
            {
                Console.WriteLine($"=== PAIEMENT TRAITER: Commande {commandeId}, Méthode {methode} ===");

                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (clientId == null)
                {
                    return RedirectToAction("Login", "Auth");
                }

                if (string.IsNullOrWhiteSpace(methode) || 
                    !new[] { "wave", "orange_money" }.Contains(methode.ToLower()))
                {
                    TempData["Error"] = "Méthode de paiement invalide.";
                    return RedirectToAction("Index", new { commandeId });
                }

                if (string.IsNullOrWhiteSpace(numeroTelephone))
                {
                    TempData["Error"] = "Numéro de téléphone requis.";
                    return RedirectToAction("Index", new { commandeId });
                }

                var commande = await _commandeRepository.GetByIdAsync(commandeId);
                if (commande == null || commande.ClientId != clientId)
                {
                    return NotFound();
                }

                var connectionString = _configuration.GetConnectionString("DefaultConnection");
                using var connection = new Npgsql.NpgsqlConnection(connectionString);
                await connection.OpenAsync();

                // Vérifier si déjà payé
                var checkSql = "SELECT COUNT(*) FROM paiement WHERE commande_id = @commandeId";
                using var checkCmd = new Npgsql.NpgsqlCommand(checkSql, connection);
                checkCmd.Parameters.AddWithValue("commandeId", commandeId);
                var count = Convert.ToInt32(await checkCmd.ExecuteScalarAsync());
                
                if (count > 0)
                {
                    TempData["Info"] = "Cette commande a déjà été payée.";
                    return RedirectToAction("Success", new { commandeId });
                }

                // Simulation paiement
                var random = new Random();
                var success = random.Next(1, 11) <= 9;

                if (!success)
                {
                    TempData["Error"] = "Le paiement a échoué. Veuillez réessayer.";
                    return RedirectToAction("Index", new { commandeId });
                }

                var reference = $"{methode.ToUpper()}-{DateTime.UtcNow:yyyyMMddHHmmss}-{commandeId}";

                var sql = @"
                    INSERT INTO paiement (commande_id, montant, methode, numero_telephone, reference_transaction, date_paiement)
                    VALUES (@commandeId, @montant, @methode, @numeroTelephone, @reference, @datePaiement)";

                using var cmd = new Npgsql.NpgsqlCommand(sql, connection);
                cmd.Parameters.AddWithValue("commandeId", commandeId);
                cmd.Parameters.AddWithValue("montant", commande.MontantTotal);
                cmd.Parameters.AddWithValue("methode", methode.ToLower());
                cmd.Parameters.AddWithValue("numeroTelephone", numeroTelephone);
                cmd.Parameters.AddWithValue("reference", reference);
                cmd.Parameters.AddWithValue("datePaiement", DateTime.UtcNow);

                await cmd.ExecuteNonQueryAsync();

                Console.WriteLine($"SUCCESS: Paiement créé");
                TempData["Success"] = "Paiement effectué avec succès !";
                
                return RedirectToAction("Success", new { commandeId });
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERROR in Paiement.Traiter: {ex.Message}");
                TempData["Error"] = "Erreur lors du paiement.";
                return RedirectToAction("Index", new { commandeId });
            }
        }

        public async Task<IActionResult> Success(int commandeId)
        {
            try
            {
                var clientId = HttpContext.Session.GetInt32("ClientId");
                if (clientId == null)
                {
                    return RedirectToAction("Login", "Auth");
                }

                var commande = await _commandeRepository.GetByIdAsync(commandeId);
                if (commande == null || commande.ClientId != clientId)
                {
                    return NotFound();
                }

                // Récupérer paiement via SQL direct
                var connectionString = _configuration.GetConnectionString("DefaultConnection");
                using var connection = new Npgsql.NpgsqlConnection(connectionString);
                await connection.OpenAsync();

                var sql = @"
                    SELECT id, commande_id, montant, methode, numero_telephone, reference_transaction, date_paiement
                    FROM paiement 
                    WHERE commande_id = @commandeId 
                    LIMIT 1";

                using var cmd = new Npgsql.NpgsqlCommand(sql, connection);
                cmd.Parameters.AddWithValue("commandeId", commandeId);

                Paiement? paiement = null;
                using var reader = await cmd.ExecuteReaderAsync();
                if (await reader.ReadAsync())
                {
                    paiement = new Paiement
                    {
                        Id = reader.GetInt32(0),
                        CommandeId = reader.GetInt32(1),
                        Montant = reader.GetDecimal(2),
                        Methode = reader.GetString(3),
                        NumeroTelephone = reader.IsDBNull(4) ? null : reader.GetString(4),
                        ReferenceTransaction = reader.IsDBNull(5) ? null : reader.GetString(5),
                        DatePaiement = reader.GetDateTime(6)
                    };
                }

                ViewBag.Commande = commande;
                ViewBag.Paiement = paiement;

                return View();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERROR in Paiement.Success: {ex.Message}");
                return RedirectToAction("MesCommandes", "Commande");
            }
        }
    }
}