<?php

namespace App\DTO\Response;

use App\Entity\Commande;

class CommandeListDTO
{
    public int $id;
    public string $clientNom;
    public \DateTime $dateCommande;
    public float $montantTotal;
    public string $typeRecuperation;
    public string $etat;
    public ?string $adresseLivraison;

    public function __construct(array $data)
    {
        $this->id = $data['id'];
        $this->clientNom = $data['client_nom'] ?? 'Inconnu';
        $this->dateCommande = new \DateTime($data['date_commande']);
        $this->montantTotal = (float) $data['montant_total'];
        $this->typeRecuperation = $data['type_recuperation'];
        $this->etat = $data['etat'];
        $this->adresseLivraison = $data['adresse_livraison'];
    }
}
