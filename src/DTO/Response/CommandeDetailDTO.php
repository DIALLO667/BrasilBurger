<?php

namespace App\DTO\Response;

class CommandeDetailDTO
{
    public int $id;
    public array $client; // [nom, prenom, email, telephone]
    public \DateTime $dateCommande;
    public float $montantTotal;
    public string $typeRecuperation;
    public string $etat;
    public ?string $adresseLivraison;
    public array $lignes = []; // [{produit, quantite, prix}, ...]
    public ?array $paiement = null; // [methode, date, montant]
    public ?array $livreur = null;
    public ?array $zone = null;

    public function __construct(array $commande, array $client, array $lignes, ?array $paiement = null, ?array $livreur = null, ?array $zone = null)
    {
        $this->id = $commande['id'];
        $this->client = $client;
        $this->dateCommande = new \DateTime($commande['date_commande']);
        $this->montantTotal = (float) $commande['montant_total'];
        $this->typeRecuperation = $commande['type_recuperation'];
        $this->etat = $commande['etat'];
        $this->adresseLivraison = $commande['adresse_livraison'];
        $this->lignes = $lignes;
        $this->paiement = $paiement;
        $this->livreur = $livreur;
        $this->zone = $zone;
    }
}
