using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.ClientApp.Models
{
    [Table("lignecommande")]
    public class LigneCommande
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [Column("commande_id")]
        public int CommandeId { get; set; }

        [Required]
        [StringLength(20)]
        [Column("produit_type")]
        public string ProduitType { get; set; } = string.Empty; // "burger" ou "menu"

        [Required]
        [Column("produit_id")]
        public int ProduitId { get; set; }

        [Required]
        [Column("quantite")]
        public int Quantite { get; set; }

        [Required]
        [Column("prix_unitaire", TypeName = "decimal(10,2)")]
        public decimal PrixUnitaire { get; set; }



        [Column("complements_ids")]
        public string? ComplementsIds { get; set; }

        // Relations
        [ForeignKey("CommandeId")]
        public virtual Commande? Commande { get; set; }
    }
}