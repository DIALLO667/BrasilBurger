using Microsoft.EntityFrameworkCore;
using BrasilBurger.ClientApp.Models;
namespace BrasilBurger.ClientApp.Data.Repositories
{
    public class MenuRepository
    {
        private readonly ApplicationDbContext _context;

        public MenuRepository(ApplicationDbContext context)
        {
            _context = context;
        }

        public async Task<List<Menu>> GetAllAsync()
        {
            return await _context.Menus
                .Where(m => !m.Archive)
                .OrderBy(m => m.Nom)
                .ToListAsync();
        }

        public async Task<Menu?> GetByIdAsync(int id)
        {
            return await _context.Menus
                .FirstOrDefaultAsync(m => m.Id == id && !m.Archive);
        }

        public async Task<Menu?> GetByIdWithDetailsAsync(int id)
        {
            return await _context.Menus
                .Include(m => m.Burger)
                .Include(m => m.Boisson)
                .Include(m => m.Frites)
                .FirstOrDefaultAsync(m => m.Id == id && !m.Archive);
        }

        public async Task<List<Menu>> GetAllWithDetailsAsync()
        {
            return await _context.Menus
                .Include(m => m.Burger)
                .Include(m => m.Boisson)
                .Include(m => m.Frites)
                .Where(m => !m.Archive)
                .OrderBy(m => m.Nom)
                .ToListAsync();
        }
    }
}