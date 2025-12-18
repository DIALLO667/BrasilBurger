using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.ClientApp.Models
{
    [Table("commande")]
    public class Commande
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [Column("client_id")]
        public int ClientId { get; set; }

        [Column("date_commande")]
        public DateTime DateCommande { get; set; } = DateTime.UtcNow;

        [Required]
        [StringLength(20)]
        [Column("type_recuperation")]
        public string TypeRecuperation { get; set; } = string.Empty; // sur_place, emporter, livraison

        [Required]
        [StringLength(20)]
        [Column("etat")]
        public string Etat { get; set; } = "en_cours"; // en_cours, validee, terminee, annulee

        [Required]
        [Column("montant_total", TypeName = "decimal(10,2)")]
        public decimal MontantTotal { get; set; }

        [Column("adresse_livraison")]
        public string? AdresseLivraison { get; set; }

        [Column("zone_id")]
        public int? ZoneId { get; set; }

        [Column("livreur_id")]
        public int? LivreurId { get; set; }

        // Relations
        [ForeignKey("ClientId")]
        public virtual Client? Client { get; set; }

        public virtual ICollection<LigneCommande>? LignesCommande { get; set; }
        public virtual Paiement? Paiement { get; set; }
    }
}