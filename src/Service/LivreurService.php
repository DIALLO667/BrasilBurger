<?php

namespace App\Service;

use App\Repository\LivreurRepository;
use App\DTO\Response\LivreurDTO;

class LivreurService
{
    public function __construct(
        private LivreurRepository $livreurRepository
    ) {}

    public function listerLivreurs(): array
    {
        $livreurs = $this->livreurRepository->findAllWithStats();
        
        return array_map(
            fn($l) => new LivreurDTO($l),
            $livreurs
        );
    }

    public function getLivreur(int $id): ?LivreurDTO
    {
        $livreur = $this->livreurRepository->findOneById($id);
        
        return $livreur ? new LivreurDTO($livreur) : null;
    }

    public function creerLivreur(array $data): int
    {
        return $this->livreurRepository->create($data);
    }

    public function modifierLivreur(int $id, array $data): bool
    {
        return $this->livreurRepository->update($id, $data);
    }

    public function archiverLivreur(int $id): bool
    {
        return $this->livreurRepository->archive($id);
    }
}
