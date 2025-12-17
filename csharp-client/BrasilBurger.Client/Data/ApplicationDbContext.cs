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

        // DbSets (tables)
        public DbSet<Burger> Burgers { get; set; }
        public DbSet<Complement> Complements { get; set; }
        public DbSet<Menu> Menus { get; set; }
        public DbSet<Client> Clients { get; set; }
        public DbSet<Commande> Commandes { get; set; }
        public DbSet<LigneCommande> LignesCommande { get; set; }
        public DbSet<Paiement> Paiements { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);

            // Configuration relations Burger
            modelBuilder.Entity<Burger>()
                .HasKey(b => b.Id);

            // Configuration relations Complement
            modelBuilder.Entity<Complement>()
                .HasKey(c => c.Id);

            // Configuration relations Menu
            modelBuilder.Entity<Menu>()
                .HasKey(m => m.Id);

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

            // Configuration Client
            modelBuilder.Entity<Client>()
                .HasKey(c => c.Id);

            modelBuilder.Entity<Client>()
                .HasIndex(c => c.Email)
                .IsUnique();

            // Configuration Commande
            modelBuilder.Entity<Commande>()
                .HasKey(c => c.Id);

            modelBuilder.Entity<Commande>()
                .HasOne(c => c.Client)
                .WithMany(cl => cl.Commandes)
                .HasForeignKey(c => c.ClientId)
                .OnDelete(DeleteBehavior.Restrict);

            // Configuration LigneCommande
            modelBuilder.Entity<LigneCommande>()
                .HasKey(l => l.Id);

            modelBuilder.Entity<LigneCommande>()
                .HasOne(l => l.Commande)
                .WithMany(c => c.LignesCommande)
                .HasForeignKey(l => l.CommandeId)
                .OnDelete(DeleteBehavior.Cascade);

            modelBuilder.Entity<LigneCommande>()
                .HasOne(l => l.Burger)
                .WithMany()
                .HasForeignKey(l => l.BurgerId)
                .OnDelete(DeleteBehavior.Restrict);

            modelBuilder.Entity<LigneCommande>()
                .HasOne(l => l.Menu)
                .WithMany()
                .HasForeignKey(l => l.MenuId)
                .OnDelete(DeleteBehavior.Restrict);

            // Configuration Paiement
            modelBuilder.Entity<Paiement>()
                .HasKey(p => p.Id);

            modelBuilder.Entity<Paiement>()
                .HasOne(p => p.Commande)
                .WithOne(c => c.Paiement)
                .HasForeignKey<Paiement>(p => p.CommandeId)
                .OnDelete(DeleteBehavior.Cascade);
        }
    }
}