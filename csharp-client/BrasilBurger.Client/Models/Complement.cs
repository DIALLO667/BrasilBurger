using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Client.Models
{
    [Table("complement")]
    public class Complement
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [StringLength(100)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Required]
        [Column("type")]
        public string Type { get; set; } = string.Empty; // BOISSON ou FRITES

        [Required]
        [Column("prix", TypeName = "decimal(10,2)")]
        public decimal Prix { get; set; }

        [Column("image")]
        public string? Image { get; set; }

        [Column("archive")]
        public bool Archive { get; set; } = false;

        [Column("date_creation")]
        public DateTime DateCreation { get; set; } = DateTime.UtcNow;
    }
}