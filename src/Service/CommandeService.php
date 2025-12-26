<?php

namespace App\Service;

use App\Repository\CommandeRepository;
use App\DTO\Request\FiltreCommandeDTO;
use App\DTO\Response\CommandeListDTO;
use App\DTO\Response\CommandeDetailDTO;

class CommandeService
{
    public function __construct(
        private CommandeRepository $commandeRepository
    ) {}

    /**
     * Lister commandes avec filtres
     */
    public function listerCommandes(FiltreCommandeDTO $filtres): array
    {
        $commandes = $this->commandeRepository->findWithFilters($filtres);
        
        return array_map(
            fn($c) => new CommandeListDTO($c),
            $commandes
        );
    }

    /**
     * Détail d'une commande
     */
    public function getDetailCommande(int $commandeId): ?CommandeDetailDTO
    {
        $data = $this->commandeRepository->getDetailCommande($commandeId);
        
        if (!$data) {
            return null;
        }
        
        return new CommandeDetailDTO(
            commande: $data['commande'],
            client: $data['client'],
            lignes: $data['lignes'],
            paiement: $data['paiement'],
            livreur: $data['livreur'],
            zone: $data['zone']
        );
    }

    /**
     * Changer état
     */
    public function changerEtat(int $commandeId, string $nouvelEtat): bool
    {
        return $this->commandeRepository->changerEtat($commandeId, $nouvelEtat);
    }

    /**
     * Annuler
     */
    public function annulerCommande(int $commandeId): bool
    {
        return $this->commandeRepository->annulerCommande($commandeId);
    }
}
