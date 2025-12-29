<?php

namespace App\Repository;

use App\Entity\Complement;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class ComplementRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Complement::class);
    }

    public function findAll(): array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "SELECT * FROM complement WHERE NOT archive ORDER BY nom";
        return $conn->executeQuery($sql)->fetchAllAssociative();
    }

    public function findById(int $id): ?array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "SELECT * FROM complement WHERE id = :id";
        return $conn->executeQuery($sql, ['id' => $id])->fetchAssociative() ?: null;
    }

    public function create(array $data): int
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "
            INSERT INTO complement (nom, prix, image)
            VALUES (:nom, :prix, :image)
            RETURNING id
        ";
        $result = $conn->executeQuery($sql, [
            'nom' => $data['nom'],
            'prix' => $data['prix'],
            'image' => $data['image'] ?? null
        ])->fetchAssociative();
        
        return $result['id'];
    }

    public function update(int $id, array $data): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        
        if (isset($data['image'])) {
            $sql = "UPDATE complement SET nom = :nom, prix = :prix, image = :image WHERE id = :id";
            $params = [
                'id' => $id,
                'nom' => $data['nom'],
                'prix' => $data['prix'],
                'image' => $data['image']
            ];
        } else {
            $sql = "UPDATE complement SET nom = :nom, prix = :prix WHERE id = :id";
            $params = [
                'id' => $id,
                'nom' => $data['nom'],
                'prix' => $data['prix']
            ];
        }
        
        return $conn->executeStatement($sql, $params) > 0;
    }

    public function archive(int $id): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "UPDATE complement SET archive = true WHERE id = :id";
        return $conn->executeStatement($sql, ['id' => $id]) > 0;
    }
}
