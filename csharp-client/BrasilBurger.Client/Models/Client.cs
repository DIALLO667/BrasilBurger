using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace BrasilBurger.ClientApp.Models
{
    [Table("client")]
    public class Client
    {
        [Key]
        [Column("id")]
        public int Id { get; set; }

        [Required]
        [StringLength(100)]
        [Column("nom")]
        public string Nom { get; set; } = string.Empty;

        [Required]
        [StringLength(100)]
        [Column("prenom")]
        public string Prenom { get; set; } = string.Empty;

        [Required]
        [StringLength(20)]
        [Column("telephone")]
        public string Telephone { get; set; } = string.Empty;

        [Required]
        [EmailAddress]
        [StringLength(100)]
        [Column("email")]
        public string Email { get; set; } = string.Empty;

        [Required]
        [Column("mot_de_passe")]
        public string MotDePasse { get; set; } = string.Empty;
[Column("date_creation")]
public DateTime DateCreation { get; set; } = DateTime.UtcNow;

        // Relations
        public virtual ICollection<Commande>? Commandes { get; set; }
    }
}