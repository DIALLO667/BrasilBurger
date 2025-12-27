<?php

namespace App\DTO\Response;

class ClientDetailDTO
{
    public int $id;
    public string $nom;
    public string $prenom;
    public string $email;
    public string $telephone;
    public int $nombreCommandes;
    public float $totalDepense;
    public ?\DateTime $derniereCommande;

    public function __construct(array $client, array $stats)
    {
        $this->id = $client['id'];
        $this->nom = $client['nom'];
        $this->prenom = $client['prenom'];
        $this->email = $client['email'];
        $this->telephone = $client['telephone'];
        $this->nombreCommandes = $stats['nombre_commandes'] ?? 0;
        $this->totalDepense = (float) ($stats['total_depense'] ?? 0);
        $this->derniereCommande = $stats['derniere_commande'] 
            ? new \DateTime($stats['derniere_commande']) 
            : null;
    }

    public function getNomComplet(): string
    {
        return $this->prenom . ' ' . $this->nom;
    }
}
