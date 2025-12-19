using Microsoft.AspNetCore.Mvc;
using BrasilBurger.ClientApp.Data;
using BrasilBurger.ClientApp.Data.Repositories;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Controllers
{
    public class HomeController : Controller
    {
        private readonly BurgerRepository _burgerRepository;
        private readonly MenuRepository _menuRepository;
        private readonly ComplementRepository _complementRepository;

        public HomeController(ApplicationDbContext context)
        {
            _burgerRepository = new BurgerRepository(context);
            _menuRepository = new MenuRepository(context);
            _complementRepository = new ComplementRepository(context);
        }

        public async Task<IActionResult> Index()
        {
            try
            {
                // Récupérer tous les produits
                var burgers = await _burgerRepository.GetAvailableAsync();
                var menus = await _menuRepository.GetAllWithDetailsAsync();
                var complements = await _complementRepository.GetAllAsync();

                // Passer les données à la vue
                ViewBag.Burgers = burgers ?? new List<Burger>();
                ViewBag.Menus = menus ?? new List<Menu>();
                ViewBag.Complements = complements ?? new List<Complement>();

                return View();
            }
            catch (Exception ex)
            {
                Console.WriteLine($"ERROR in Home.Index: {ex.Message}");
                
                // Retourner des listes vides en cas d'erreur
                ViewBag.Burgers = new List<Burger>();
                ViewBag.Menus = new List<Menu>();
                ViewBag.Complements = new List<Complement>();
                
                return View();
            }
        }

        public IActionResult Privacy()
        {
            return View();
        }
    }
}