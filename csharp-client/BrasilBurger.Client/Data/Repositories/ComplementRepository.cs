using Microsoft.EntityFrameworkCore;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Data.Repositories
{
    public class ComplementRepository
    {
        private readonly ApplicationDbContext _context;

        public ComplementRepository(ApplicationDbContext context)
        {
            _context = context;
        }

        public async Task<List<Complement>> GetByTypeAsync(string type)
        {
            return await _context.Complements
                .Where(c => c.Type == type && !c.Archive)
                .OrderBy(c => c.Nom)
                .ToListAsync();
        }

        public async Task<Complement?> GetByIdAsync(int id)
        {
            return await _context.Complements
                .FirstOrDefaultAsync(c => c.Id == id && !c.Archive);
        }

        // ✅ AJOUTER CETTE MÉTHODE
        public async Task<List<Complement>> GetAllAsync()
        {
            return await _context.Complements
                .Where(c => !c.Archive)
                .OrderBy(c => c.Nom)
                .ToListAsync();
        }
    }
}