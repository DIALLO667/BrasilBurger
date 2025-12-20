using Microsoft.EntityFrameworkCore;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Data.Repositories
{
    public class ZoneRepository
    {
        private readonly ApplicationDbContext _context;

        public ZoneRepository(ApplicationDbContext context)
        {
            _context = context;
        }

        public async Task<List<Zone>> GetAllAsync()
        {
            return await _context.Set<Zone>()
                .OrderBy(z => z.PrixLivraison)
                .ToListAsync();
        }

        public async Task<Zone?> GetByIdAsync(int id)
        {
            return await _context.Set<Zone>()
                .FirstOrDefaultAsync(z => z.Id == id);
        }

        // Trouver zone depuis une adresse
        public async Task<Zone?> FindZoneByAddressAsync(string adresse)
        {
            if (string.IsNullOrWhiteSpace(adresse))
                return null;

            var zones = await GetAllAsync();
            var adresseLower = adresse.ToLower();

            // Chercher correspondance dans les quartiers
            foreach (var zone in zones)
            {
                if (string.IsNullOrEmpty(zone.Quartiers))
                    continue;

                var quartiers = zone.Quartiers.Split(',')
                    .Select(q => q.Trim().ToLower());

                foreach (var quartier in quartiers)
                {
                    if (adresseLower.Contains(quartier))
                    {
                        return zone;
                    }
                }
            }

            // Si aucune zone trouvée, retourner zone par défaut (la moins chère)
            return zones.FirstOrDefault();
        }
    }
}