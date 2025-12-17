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
        [StringLength(50)]
        [Column("numero_commande")]
        public string NumeroCommande { get; set; } = string.Empty;

        [Required]
        [Column("client_id")]
        public int ClientId { get; set; }

        [Column("date_commande")]
        public DateTime DateCommande { get; set; } = DateTime.UtcNow;

        [Required]
        [StringLength(20)]
        [Column("type_consommation")]
        public string TypeConsommation { get; set; } = string.Empty; // SUR_PLACE, EMPORTER, LIVRAISON

        [Required]
        [StringLength(20)]
        [Column("statut")]
        public string Statut { get; set; } = "EN_COURS"; // EN_COURS, PRETE, TERMINEE, ANNULEE

        [Required]
        [Column("montant_total", TypeName = "decimal(10,2)")]
        public decimal MontantTotal { get; set; }

        [Column("adresse_livraison")]
        public string? AdresseLivraison { get; set; }

        [Column("zone_livraison_id")]
        public int? ZoneLivraisonId { get; set; }

        [Column("livreur_id")]
        public int? LivreurId { get; set; }

        [Column("paiement_effectue")]
        public bool PaiementEffectue { get; set; } = false;

        // Relations
        [ForeignKey("ClientId")]
        public virtual Client? Client { get; set; }

        public virtual ICollection<LigneCommande>? LignesCommande { get; set; }
        public virtual Paiement? Paiement { get; set; }
    }
}