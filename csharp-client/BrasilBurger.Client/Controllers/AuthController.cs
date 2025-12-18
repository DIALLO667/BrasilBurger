using Microsoft.AspNetCore.Mvc;
using BrasilBurger.ClientApp.Data;
using BrasilBurger.ClientApp.Data.Repositories;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Controllers
{
    public class AuthController : Controller
    {
        private readonly ClientRepository _clientRepository;

        public AuthController(ApplicationDbContext context)
        {
            _clientRepository = new ClientRepository(context);
        }

        // GET: /Auth/Login
        [HttpGet]
        public IActionResult Login()
        {
            // Si déjà connecté, rediriger vers accueil
            if (HttpContext.Session.GetInt32("ClientId") != null)
            {
                return RedirectToAction("Index", "Home");
            }

            return View();
        }

        // POST: /Auth/Login
        [HttpPost]
        public async Task<IActionResult> Login(string email, string motDePasse)
        {
            if (string.IsNullOrWhiteSpace(email) || string.IsNullOrWhiteSpace(motDePasse))
            {
                ViewBag.Error = "Veuillez remplir tous les champs.";
                return View();
            }

            var client = await _clientRepository.AuthenticateAsync(email, motDePasse);

            if (client == null)
            {
                ViewBag.Error = "Email ou mot de passe incorrect.";
                return View();
            }

            // Stocker en session
            HttpContext.Session.SetInt32("ClientId", client.Id);
            HttpContext.Session.SetString("ClientNom", client.Nom);
            HttpContext.Session.SetString("ClientPrenom", client.Prenom);
            HttpContext.Session.SetString("ClientEmail", client.Email);

            TempData["Success"] = $"Bienvenue {client.Prenom} !";
            return RedirectToAction("Index", "Home");
        }

        // GET: /Auth/Register
        [HttpGet]
        public IActionResult Register()
        {
            // Si déjà connecté, rediriger vers accueil
            if (HttpContext.Session.GetInt32("ClientId") != null)
            {
                return RedirectToAction("Index", "Home");
            }

            return View();
        }

        // POST: /Auth/Register
        [HttpPost]
        public async Task<IActionResult> Register(string nom, string prenom, string telephone, string email, string motDePasse, string confirmMotDePasse)
        {
            // Validation
            if (string.IsNullOrWhiteSpace(nom) || string.IsNullOrWhiteSpace(prenom) || 
                string.IsNullOrWhiteSpace(telephone) || string.IsNullOrWhiteSpace(email) || 
                string.IsNullOrWhiteSpace(motDePasse))
            {
                ViewBag.Error = "Tous les champs sont obligatoires.";
                return View();
            }

            if (motDePasse != confirmMotDePasse)
            {
                ViewBag.Error = "Les mots de passe ne correspondent pas.";
                return View();
            }

            if (motDePasse.Length < 6)
            {
                ViewBag.Error = "Le mot de passe doit contenir au moins 6 caractères.";
                return View();
            }

            // Vérifier si email existe déjà
            var existingClient = await _clientRepository.GetByEmailAsync(email);
            if (existingClient != null)
            {
                ViewBag.Error = "Cet email est déjà utilisé.";
                return View();
            }

            // Créer le client
            var client = new Client
            {
                Nom = nom,
                Prenom = prenom,
                Telephone = telephone,
                Email = email,
                MotDePasse = motDePasse, // TODO: Hash en production !
                 DateCreation = DateTime.UtcNow
            };

            await _clientRepository.CreateAsync(client);

            // Connexion automatique
            HttpContext.Session.SetInt32("ClientId", client.Id);
            HttpContext.Session.SetString("ClientNom", client.Nom);
            HttpContext.Session.SetString("ClientPrenom", client.Prenom);
            HttpContext.Session.SetString("ClientEmail", client.Email);

            TempData["Success"] = "Compte créé avec succès ! Bienvenue 🎉";
            return RedirectToAction("Index", "Home");
        }

        // GET: /Auth/Logout
        public IActionResult Logout()
        {
            HttpContext.Session.Clear();
            TempData["Success"] = "Vous êtes déconnecté.";
            return RedirectToAction("Index", "Home");
        }
    }
}