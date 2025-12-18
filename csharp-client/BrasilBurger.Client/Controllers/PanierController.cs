using Microsoft.AspNetCore.Mvc;
using BrasilBurger.ClientApp.Data;
using BrasilBurger.ClientApp.Data.Repositories;
using BrasilBurger.ClientApp.Models;
using BrasilBurger.ClientApp.Helpers;

namespace BrasilBurger.ClientApp.Controllers
{
    public class PanierController : Controller
    {
        private readonly BurgerRepository _burgerRepository;
        private readonly MenuRepository _menuRepository;

        public PanierController(ApplicationDbContext context)
        {
            _burgerRepository = new BurgerRepository(context);
            _menuRepository = new MenuRepository(context);
        }

        // GET: /Panier
        public IActionResult Index()
        {
            var panier = HttpContext.Session.GetPanier();
            ViewBag.Total = HttpContext.Session.TotalPanier();
            return View(panier);
        }

        // POST: /Panier/Ajouter
        [HttpPost]
        public async Task<IActionResult> Ajouter(string type, int id, int quantite = 1)
        {
            PanierItem item = new PanierItem();

            if (type.ToLower() == "burger")
            {
                var burger = await _burgerRepository.GetByIdAsync(id);
                if (burger == null) return NotFound();

                item = new PanierItem
                {
                    Type = "burger",
                    ProduitId = burger.Id,
                    Nom = burger.Nom,
                    Prix = burger.Prix,
                    Quantite = quantite,
                    Image = burger.Image
                };
            }
            else if (type.ToLower() == "menu")
            {
                var menu = await _menuRepository.GetByIdWithDetailsAsync(id);
                if (menu == null) return NotFound();

                item = new PanierItem
                {
                    Type = "menu",
                    ProduitId = menu.Id,
                    Nom = menu.Nom,
                    Prix = menu.PrixCalcule,
                    Quantite = quantite,
                    Image = menu.Image,
                    CompositionBurger = menu.Burger?.Nom,
                    CompositionBoisson = menu.Boisson?.Nom,
                    CompositionFrites = menu.Frites?.Nom
                };
            }
            else
            {
                return BadRequest("Type invalide");
            }

            HttpContext.Session.AjouterAuPanier(item);

            TempData["Success"] = $"{item.Nom} ajouté au panier !";
            return RedirectToAction("Index");
        }

        // POST: /Panier/Retirer
        [HttpPost]
        public IActionResult Retirer(int index)
        {
            HttpContext.Session.RetirerDuPanier(index);
            TempData["Success"] = "Produit retiré du panier.";
            return RedirectToAction("Index");
        }

        // POST: /Panier/UpdateQuantite
        [HttpPost]
        public IActionResult UpdateQuantite(int index, int quantite)
        {
            if (quantite <= 0)
            {
                return RedirectToAction("Retirer", new { index });
            }

            var panier = HttpContext.Session.GetPanier();
            if (index >= 0 && index < panier.Count)
            {
                panier[index].Quantite = quantite;
                HttpContext.Session.SetPanier(panier);
            }

            return RedirectToAction("Index");
        }

        // POST: /Panier/Vider
        [HttpPost]
        public IActionResult Vider()
        {
            HttpContext.Session.ViderPanier();
            TempData["Success"] = "Panier vidé.";
            return RedirectToAction("Index");
        }
    }
}