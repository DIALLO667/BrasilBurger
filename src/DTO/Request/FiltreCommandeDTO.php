<?php

namespace App\DTO\Request;

class FiltreCommandeDTO
{
    public ?string $etat = null;
    public ?string $type = null; // burger, menu, livraison, sur_place, emporter
    public ?\DateTime $dateDebut = null;
    public ?\DateTime $dateFin = null;
    public ?string $clientNom = null;
}
