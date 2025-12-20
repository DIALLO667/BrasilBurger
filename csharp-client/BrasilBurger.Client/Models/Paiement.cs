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

        [Required]
        [Column("montant", TypeName = "decimal(10,2)")]
        public decimal Montant { get; set; }

        [Required]
        [StringLength(50)]
        [Column("methode")]
        public string Methode { get; set; } = string.Empty;

        [StringLength(20)]
        [Column("numero_telephone")]
        public string? NumeroTelephone { get; set; }

        [StringLength(100)]
        [Column("reference_transaction")]
        public string? ReferenceTransaction { get; set; }

        [Column("date_paiement")]
        public DateTime DatePaiement { get; set; } = DateTime.UtcNow;

        // Navigation property (PAS de ForeignKey attribute ici)
        public Commande? Commande { get; set; }
    }
}