using Microsoft.EntityFrameworkCore;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Data
{
    public class ApplicationDbContext : DbContext
    {
        public ApplicationDbContext(DbContextOptions<ApplicationDbContext> options)
            : base(options)
        {
        }

        public DbSet<Client> Clients { get; set; }
        public DbSet<Burger> Burgers { get; set; }
        public DbSet<Menu> Menus { get; set; }
        public DbSet<Complement> Complements { get; set; }
        public DbSet<Commande> Commandes { get; set; }
        public DbSet<LigneCommande> LignesCommande { get; set; }
        public DbSet<Paiement> Paiements { get; set; }
        public DbSet<Zone> Zones { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            modelBuilder.Entity<Menu>()
                .HasOne(m => m.Burger)
                .WithMany()
                .HasForeignKey(m => m.BurgerId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Menu>()
                .HasOne(m => m.Boisson)
                .WithMany()
                .HasForeignKey(m => m.BoissonId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<Menu>()
                .HasOne(m => m.Frites)
                .WithMany()
                .HasForeignKey(m => m.FritesId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<LigneCommande>()
                .HasOne(l => l.Commande)
                .WithMany(c => c.LignesCommande)
                .HasForeignKey(l => l.CommandeId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<Paiement>()
                .HasOne(p => p.Commande)
                .WithMany()
                .HasForeignKey(p => p.CommandeId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}