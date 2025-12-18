using Microsoft.AspNetCore.Http;
using System.Text.Json;
using BrasilBurger.ClientApp.Models;

namespace BrasilBurger.ClientApp.Helpers
{
    public static class SessionExtensions
    {
        // Clé session panier
        private const string PanierKey = "Panier";

        // Récupérer le panier
        public static List<PanierItem> GetPanier(this ISession session)
        {
            var panierJson = session.GetString(PanierKey);
            if (string.IsNullOrEmpty(panierJson))
            {
                return new List<PanierItem>();
            }
            return JsonSerializer.Deserialize<List<PanierItem>>(panierJson) ?? new List<PanierItem>();
        }

        // Sauvegarder le panier
        public static void SetPanier(this ISession session, List<PanierItem> panier)
        {
            var panierJson = JsonSerializer.Serialize(panier);
            session.SetString(PanierKey, panierJson);
        }

        // Ajouter un produit
        public static void AjouterAuPanier(this ISession session, PanierItem item)
        {
            var panier = session.GetPanier();

            // Vérifier si le produit existe déjà
            var existant = panier.FirstOrDefault(p => p.Type == item.Type && p.ProduitId == item.ProduitId);

            if (existant != null)
            {
                // Augmenter la quantité
                existant.Quantite += item.Quantite;
            }
            else
            {
                // Ajouter nouveau produit
                panier.Add(item);
            }

            session.SetPanier(panier);
        }

        // Retirer un produit
        public static void RetirerDuPanier(this ISession session, int index)
        {
            var panier = session.GetPanier();
            if (index >= 0 && index < panier.Count)
            {
                panier.RemoveAt(index);
                session.SetPanier(panier);
            }
        }

        // Vider le panier
        public static void ViderPanier(this ISession session)
        {
            session.Remove(PanierKey);
        }

        // Compter items
        public static int CompterItemsPanier(this ISession session)
        {
            return session.GetPanier().Sum(p => p.Quantite);
        }

        // Calculer total
        public static decimal TotalPanier(this ISession session)
        {
            return session.GetPanier().Sum(p => p.Total);
        }
    }
}