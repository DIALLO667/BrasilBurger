using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.ClientApp.Models
{
    [Table("zone")]
    public class Zone
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [StringLength(100)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Column("quartiers")]
        public string? Quartiers { get; set; }

        [Required]
        [Column("prix_livraison", TypeName = "decimal(10,2)")]
        public decimal PrixLivraison { get; set; }
    }
}