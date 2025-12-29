<?php

namespace App\Repository;

use App\Entity\Menu;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class MenuRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Menu::class);
    }

    public function findAll(): array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "SELECT * FROM menu WHERE NOT archive ORDER BY nom";
        return $conn->executeQuery($sql)->fetchAllAssociative();
    }

    public function findById(int $id): ?array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "SELECT * FROM menu WHERE id = :id";
        return $conn->executeQuery($sql, ['id' => $id])->fetchAssociative() ?: null;
    }

    public function create(array $data): int
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "
            INSERT INTO menu (nom, image)
            VALUES (:nom, :image)
            RETURNING id
        ";
        $result = $conn->executeQuery($sql, [
            'nom' => $data['nom'],
            'image' => $data['image'] ?? null
        ])->fetchAssociative();
        
        return $result['id'];
    }

    public function update(int $id, array $data): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        
        if (isset($data['image'])) {
            $sql = "UPDATE menu SET nom = :nom, image = :image WHERE id = :id";
            $params = [
                'id' => $id,
                'nom' => $data['nom'],
                'image' => $data['image']
            ];
        } else {
            $sql = "UPDATE menu SET nom = :nom WHERE id = :id";
            $params = [
                'id' => $id,
                'nom' => $data['nom']
            ];
        }
        
        return $conn->executeStatement($sql, $params) > 0;
    }

    public function archive(int $id): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "UPDATE menu SET archive = true WHERE id = :id";
        return $conn->executeStatement($sql, ['id' => $id]) > 0;
    }
}
