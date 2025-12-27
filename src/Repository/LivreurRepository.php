<?php

namespace App\Repository;

use App\Entity\Livreur;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class LivreurRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Livreur::class);
    }

    /**
     * Liste tous les livreurs avec leurs zones et nombre de commandes
     */
    public function findAllWithStats(): array
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            SELECT 
                l.*,
                z.nom as zone_nom,
                COUNT(c.id) as commandes_en_cours
            FROM livreur l
            LEFT JOIN zone z ON z.id = l.zone_id
            LEFT JOIN commande c ON c.livreur_id = l.id AND c.etat = 'en_cours'
            WHERE NOT l.archive
            GROUP BY l.id, z.nom
            ORDER BY l.nom
        ";
        
        return $conn->executeQuery($sql)->fetchAllAssociative();
    }

    /**
     * Trouver livreur par ID
     */
    public function findOneById(int $id): ?array
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "SELECT l.*, z.nom as zone_nom FROM livreur l LEFT JOIN zone z ON z.id = l.zone_id WHERE l.id = :id";
        
        return $conn->executeQuery($sql, ['id' => $id])->fetchAssociative() ?: null;
    }

    /**
     * Créer un livreur
     */
    public function create(array $data): int
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            INSERT INTO livreur (nom, prenom, telephone, zone_id, date_creation)
            VALUES (:nom, :prenom, :telephone, :zoneId, CURRENT_TIMESTAMP)
            RETURNING id
        ";
        
        $result = $conn->executeQuery($sql, [
            'nom' => $data['nom'],
            'prenom' => $data['prenom'],
            'telephone' => $data['telephone'],
            'zoneId' => $data['zone_id'] ?? null
        ])->fetchAssociative();
        
        return $result['id'];
    }

    /**
     * Mettre à jour un livreur
     */
    public function update(int $id, array $data): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            UPDATE livreur 
            SET nom = :nom, prenom = :prenom, telephone = :telephone, zone_id = :zoneId
            WHERE id = :id
        ";
        
        return $conn->executeStatement($sql, [
            'id' => $id,
            'nom' => $data['nom'],
            'prenom' => $data['prenom'],
            'telephone' => $data['telephone'],
            'zoneId' => $data['zone_id'] ?? null
        ]) > 0;
    }

    /**
     * Archiver un livreur
     */
    public function archive(int $id): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "UPDATE livreur SET archive = true WHERE id = :id";
        
        return $conn->executeStatement($sql, ['id' => $id]) > 0;
    }
}
