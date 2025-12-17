using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Client.Models
{
    [Table("ligne_commande")]
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
        [Column("type_produit")]
        public string TypeProduit { get; set; } = string.Empty; // BURGER ou MENU

        [Column("burger_id")]
        public int? BurgerId { get; set; }

        [Column("menu_id")]
        public int? MenuId { get; set; }

        [Required]
        [Column("quantite")]
        public int Quantite { get; set; }

        [Required]
        [Column("prix_unitaire", TypeName = "decimal(10,2)")]
        public decimal PrixUnitaire { get; set; }

        [Required]
        [Column("prix_total", TypeName = "decimal(10,2)")]
        public decimal PrixTotal { get; set; }

        // Relations
        [ForeignKey("CommandeId")]
        public virtual Commande? Commande { get; set; }

        [ForeignKey("BurgerId")]
        public virtual Burger? Burger { get; set; }

        [ForeignKey("MenuId")]
        public virtual Menu? Menu { get; set; }
    }
}