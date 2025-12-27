<?php

namespace App\DTO\Response;

class LivreurDTO
{
    public int $id;
    public string $nom;
    public string $prenom;
    public string $telephone;
    public ?int $zoneId;
    public ?string $zoneNom;
    public int $commandesEnCours;

    public function __construct(array $data)
    {
        $this->id = $data['id'];
        $this->nom = $data['nom'];
        $this->prenom = $data['prenom'];
        $this->telephone = $data['telephone'];
        $this->zoneId = $data['zone_id'] ?? null;
        $this->zoneNom = $data['zone_nom'] ?? null;
        $this->commandesEnCours = $data['commandes_en_cours'] ?? 0;
    }

    public function getNomComplet(): string
    {
        return $this->prenom . ' ' . $this->nom;
    }
}
