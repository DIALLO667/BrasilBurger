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
        [Column("mode")]
        public string Mode { get; set; } = string.Empty; // WAVE ou ORANGE_MONEY

        [Required]
        [StringLength(20)]
        [Column("statut")]
        public string Statut { get; set; } = "REUSSI"; // REUSSI ou ECHOUE

        // Relations
        [ForeignKey("CommandeId")]
        public virtual Commande? Commande { get; set; }
    }
}