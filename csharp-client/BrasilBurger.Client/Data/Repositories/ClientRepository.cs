using Microsoft.EntityFrameworkCore;
// using BrasilBurger.ClientApp.Models;
using BrasilBurger.ClientApp.Models;
namespace BrasilBurger.ClientApp.Data.Repositories
{
    public class ClientRepository
    {
        private readonly ApplicationDbContext _context;

        public ClientRepository(ApplicationDbContext context)
        {
            _context = context;
        }

        public async Task<Client?> GetByEmailAsync(string email)
        {
            return await _context.Clients
                .FirstOrDefaultAsync(c => c.Email == email);
        }

        public async Task<Client?> GetByIdAsync(int id)
        {
            return await _context.Clients
                .FirstOrDefaultAsync(c => c.Id == id);
        }

        public async Task<Client> CreateAsync(Client client)
        {
            _context.Clients.Add(client);
            await _context.SaveChangesAsync();
            return client;
        }

        public async Task<Client?> AuthenticateAsync(string email, string motDePasse)
        {
            return await _context.Clients
                .FirstOrDefaultAsync(c => c.Email == email && c.MotDePasse == motDePasse);
        }
    }
}