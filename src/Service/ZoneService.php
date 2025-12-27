<?php

namespace App\Service;

use App\Repository\ZoneRepository;
use App\Repository\LivreurRepository;
use App\DTO\Response\ZoneGroupDTO;
use App\DTO\Response\LivreurDTO;

class ZoneService
{
    public function __construct(
        private ZoneRepository $zoneRepository,
        private LivreurRepository $livreurRepository
    ) {}

    /**
     * Regrouper commandes par zone
     */
    public function getCommandesParZone(): array
    {
        $zonesData = $this->zoneRepository->getCommandesGroupeesParZone();
        
        $groups = [];
        
        foreach ($zonesData as $zoneData) {
            $commandes = $this->zoneRepository->getCommandesZone($zoneData['id']);
            
            $livreur = null;
            if ($zoneData['livreur_id']) {
                $livreurData = $this->livreurRepository->findOneById($zoneData['livreur_id']);
                if ($livreurData) {
                    $livreur = new LivreurDTO($livreurData);
                }
            }
            
            $groups[] = new ZoneGroupDTO($zoneData, $commandes, $livreur);
        }
        
        return $groups;
    }

    /**
     * Affecter livreur à une zone
     */
    public function affecterLivreur(int $zoneId, int $livreurId): int
    {
        return $this->zoneRepository->affecterLivreurZone($zoneId, $livreurId);
    }

    /**
     * Liste toutes les zones
     */
    public function listerZones(): array
    {
        return $this->zoneRepository->findAllZones();
    }
}
