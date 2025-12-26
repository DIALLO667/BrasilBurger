<?php

namespace App\DTO\Response;

class StatistiquesDTO
{
    public int $commandesEnCours;
    public int $commandesValidees;
    public float $recettesJournalieres;
    public int $commandesAnnulees;
    public array $topBurgers = []; // [{nom, quantite}, ...]
    public array $topMenus = [];

    public function __construct(
        int $commandesEnCours = 0,
        int $commandesValidees = 0,
        float $recettesJournalieres = 0,
        int $commandesAnnulees = 0,
        array $topBurgers = [],
        array $topMenus = []
    ) {
        $this->commandesEnCours = $commandesEnCours;
        $this->commandesValidees = $commandesValidees;
        $this->recettesJournalieres = $recettesJournalieres;
        $this->commandesAnnulees = $commandesAnnulees;
        $this->topBurgers = $topBurgers;
        $this->topMenus = $topMenus;
    }
}
