using Microsoft.AspNetCore.Mvc;

namespace BrasilBurger.ClientApp.Controllers
{
    public class ProfilController : Controller
    {
        // GET: /Profil
        public IActionResult Index()
        {
            // Vérifier si connecté
            if (HttpContext.Session.GetInt32("ClientId") == null)
            {
                return RedirectToAction("Login", "Auth");
            }

            return View();
        }
    }
}