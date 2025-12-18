using Microsoft.AspNetCore.Mvc;
using BrasilBurger.ClientApp.Data;
using BrasilBurger.ClientApp.Data.Repositories;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Controllers
{
    public class BurgerController : Controller
    {
        private readonly BurgerRepository _burgerRepository;

        public BurgerController(ApplicationDbContext context)
        {
            _burgerRepository = new BurgerRepository(context);
        }

        // GET: /Burger
        public async Task<IActionResult> Index()
        {
            var burgers = await _burgerRepository.GetAvailableAsync();
            return View(burgers);
        }

        // GET: /Burger/Detail/5
        public async Task<IActionResult> Detail(int id)
        {
            var burger = await _burgerRepository.GetByIdAsync(id);
            
            if (burger == null)
            {
                return NotFound();
            }

            return View(burger);
        }
    }
}