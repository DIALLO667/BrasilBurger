using Microsoft.EntityFrameworkCore;
using BrasilBurger.ClientApp.Models;
namespace BrasilBurger.ClientApp.Data.Repositories
{
    public class BurgerRepository
    {
        private readonly ApplicationDbContext _context;

        public BurgerRepository(ApplicationDbContext context)
        {
            _context = context;
        }

        public async Task<List<Burger>> GetAllAsync()
        {
            return await _context.Burgers
                .Where(b => !b.Archive)
                .OrderBy(b => b.Nom)
                .ToListAsync();
        }

        public async Task<Burger?> GetByIdAsync(int id)
        {
            return await _context.Burgers
                .FirstOrDefaultAsync(b => b.Id == id && !b.Archive);
        }

        public async Task<List<Burger>> GetAvailableAsync()
        {
            return await _context.Burgers
                .Where(b => !b.Archive)
                .OrderBy(b => b.Nom)
                .ToListAsync();
        }
    }
}