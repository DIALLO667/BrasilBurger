using Microsoft.EntityFrameworkCore;
using BrasilBurger.ClientApp.Models;
namespace BrasilBurger.ClientApp.Data.Repositories
{
    public class CommandeRepository
    {
        private readonly ApplicationDbContext _context;

        public CommandeRepository(ApplicationDbContext context)
        {
            _context = context;
        }

        public async Task<Commande> CreateAsync(Commande commande)
        {
            _context.Commandes.Add(commande);
            await _context.SaveChangesAsync();
            return commande;
        }

        public async Task<List<Commande>> GetByClientIdAsync(int clientId)
        {
            return await _context.Commandes
                .Include(c => c.LignesCommande)
                .Where(c => c.ClientId == clientId)
                .OrderByDescending(c => c.DateCommande)
                .ToListAsync();
        }

public async Task<Commande?> GetByIdAsync(int id)
{
    return await _context.Commandes
        .Include(c => c.LignesCommande!)
            .ThenInclude(l => l.Burger)
        .Include(c => c.LignesCommande!)
            .ThenInclude(l => l.Menu)
        .Include(c => c.Paiement)
        .FirstOrDefaultAsync(c => c.Id == id);
}

        public async Task UpdateAsync(Commande commande)
        {
            _context.Commandes.Update(commande);
            await _context.SaveChangesAsync();
        }
    }
}