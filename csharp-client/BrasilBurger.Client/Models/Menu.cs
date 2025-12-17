using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.Client.Models
{
    [Table("menu")]
    public class Menu
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [StringLength(100)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Column("image")]
        public string? Image { get; set; }

        [Required]
        [Column("burger_id")]
        public int BurgerId { get; set; }

        [Required]
        [Column("boisson_id")]
        public int BoissonId { get; set; }

        [Required]
        [Column("frites_id")]
        public int FritesId { get; set; }

        [Column("prix_calcule", TypeName = "decimal(10,2)")]
        public decimal PrixCalcule { get; set; }

        [Column("archive")]
        public bool Archive { get; set; } = false;

        [Column("date_creation")]
        public DateTime DateCreation { get; set; } = DateTime.UtcNow;

        // Relations (navigation properties)
        [ForeignKey("BurgerId")]
        public virtual Burger? Burger { get; set; }

        [ForeignKey("BoissonId")]
        public virtual Complement? Boisson { get; set; }

        [ForeignKey("FritesId")]
        public virtual Complement? Frites { get; set; }
    }
}