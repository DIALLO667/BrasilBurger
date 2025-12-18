using Microsoft.AspNetCore.Mvc;
using BrasilBurger.ClientApp.Data;
using BrasilBurger.ClientApp.Data.Repositories;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Controllers
{
    public class MenuController : Controller
    {
        private readonly MenuRepository _menuRepository;

        public MenuController(ApplicationDbContext context)
        {
            _menuRepository = new MenuRepository(context);
        }

        // GET: /Menu
        public async Task<IActionResult> Index()
        {
            var menus = await _menuRepository.GetAllWithDetailsAsync();
            return View(menus);
        }

        // GET: /Menu/Detail/5
        public async Task<IActionResult> Detail(int id)
        {
            var menu = await _menuRepository.GetByIdWithDetailsAsync(id);
            
            if (menu == null)
            {
                return NotFound();
            }

            return View(menu);
        }
    }
}