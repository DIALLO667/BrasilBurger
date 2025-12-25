<?php

namespace App\DTO\Response;

use App\Entity\Gestionnaire;

class GestionnaireDTO
{
    public int $id;
    public string $nom;
    public string $prenom;
    public string $email;
    public string $nomComplet;

    public function __construct(Gestionnaire $gestionnaire)
    {
        $this->id = $gestionnaire->getId();
        $this->nom = $gestionnaire->getNom();
        $this->prenom = $gestionnaire->getPrenom();
        $this->email = $gestionnaire->getEmail();
        $this->nomComplet = $this->prenom . ' ' . $this->nom;
    }
}
