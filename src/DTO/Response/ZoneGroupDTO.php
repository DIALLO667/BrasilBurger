<?php

namespace App\DTO\Response;

class ZoneGroupDTO
{
    public int $zoneId;
    public string $zoneNom;
    public float $prixLivraison;
    public array $commandes = []; // CommandeListDTO[]
    public int $nombreCommandes;
    public ?LivreurDTO $livreurAffecte = null;

    public function __construct(array $zone, array $commandes, ?LivreurDTO $livreur = null)
    {
        $this->zoneId = $zone['id'];
        $this->zoneNom = $zone['nom'];
        $this->prixLivraison = (float) $zone['prix_livraison'];
        $this->commandes = $commandes;
        $this->nombreCommandes = count($commandes);
        $this->livreurAffecte = $livreur;
    }
}
