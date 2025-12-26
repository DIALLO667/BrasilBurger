<?php

namespace App\Service;

use App\Repository\CommandeRepository;
use App\DTO\Response\StatistiquesDTO;

class StatistiqueService
{
    public function __construct(
        private CommandeRepository $commandeRepository
    ) {}

    public function getStatistiquesJour(): StatistiquesDTO
    {
        $commandesEnCours = $this->commandeRepository->countCommandesEnCoursJour();
        $commandesValidees = $this->commandeRepository->countCommandesValideesJour();
        $recettes = $this->commandeRepository->getRecettesJournalieres();
        $commandesAnnulees = $this->commandeRepository->countCommandesAnnuleesJour();
        $topBurgers = $this->commandeRepository->getTopBurgersJour(5);
        $topMenus = $this->commandeRepository->getTopMenusJour(5);

        return new StatistiquesDTO(
            commandesEnCours: $commandesEnCours,
            commandesValidees: $commandesValidees,
            recettesJournalieres: $recettes,
            commandesAnnulees: $commandesAnnulees,
            topBurgers: $topBurgers,
            topMenus: $topMenus
        );
    }
}
