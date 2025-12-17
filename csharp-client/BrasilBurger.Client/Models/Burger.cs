using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Client.Models
{
    [Table("burger")]
    public class Burger
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [StringLength(100)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Required]
        [Column("prix", TypeName = "decimal(10,2)")]
        public decimal Prix { get; set; }

        [Column("image")]
        public string? Image { get; set; }

        [Column("description")]
        public string? Description { get; set; }

        [Column("archive")]
        public bool Archive { get; set; } = false;

        [Column("date_creation")]
        public DateTime DateCreation { get; set; } = DateTime.UtcNow;
    }
}