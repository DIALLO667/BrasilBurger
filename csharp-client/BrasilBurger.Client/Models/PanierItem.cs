namespace BrasilBurger.ClientApp.Models
{
    public class PanierItem
    {
        public string Type { get; set; } = string.Empty; // "burger" ou "menu"
        public int ProduitId { get; set; }
        public string Nom { get; set; } = string.Empty;
        public decimal Prix { get; set; }
        public int Quantite { get; set; } = 1;
        public string? Image { get; set; }

        // Pour les menus : composition
        public string? CompositionBurger { get; set; }
        public string? CompositionBoisson { get; set; }
        public string? CompositionFrites { get; set; }

        // Calcul total ligne
        public decimal Total => Prix * Quantite;
    }
}