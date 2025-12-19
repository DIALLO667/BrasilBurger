using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.ClientApp.Models
{
    [Table("paiement")]
    public class Paiement
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [Column("commande_id")]
        public int CommandeId { get; set; }

        [Column("date_paiement")]
        public DateTime DatePaiement { get; set; } = DateTime.UtcNow;

        [Required]
        [Column("montant", TypeName = "decimal(10,2)")]
        public decimal Montant { get; set; }

        [Required]
        [StringLength(20)]
        [Column("methode")]
        public string Methode { get; set; } = string.Empty; // "wave" ou "om"

        [Column("reference_transaction")]
        [StringLength(100)]
        public string? ReferenceTransaction { get; set; }

        // Relation
        [ForeignKey("CommandeId")]
        public virtual Commande? Commande { get; set; }
    }
}