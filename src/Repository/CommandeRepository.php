<?php

namespace App\Repository;

use App\Entity\Commande;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class CommandeRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Commande::class);
    }

    /**
     * Compter commandes en cours du jour
     */
    public function countCommandesEnCoursJour(): int
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "
            SELECT COUNT(*) as total
            FROM commande
            WHERE DATE(date_commande) = CURRENT_DATE
            AND etat = 'en_cours'
        ";
        $result = $conn->executeQuery($sql)->fetchAssociative();
        return (int) $result['total'];
    }

    /**
     * Compter commandes validées du jour
     */
    public function countCommandesValideesJour(): int
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "
            SELECT COUNT(*) as total
            FROM commande
            WHERE DATE(date_commande) = CURRENT_DATE
            AND etat IN ('terminee', 'en_preparation', 'prete')
        ";
        $result = $conn->executeQuery($sql)->fetchAssociative();
        return (int) $result['total'];
    }

    /**
     * Calculer recettes journalières (commandes payées)
     */
    public function getRecettesJournalieres(): float
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "
            SELECT COALESCE(SUM(c.montant_total), 0) as total
            FROM commande c
            INNER JOIN paiement p ON p.commande_id = c.id
            WHERE DATE(c.date_commande) = CURRENT_DATE
        ";
        $result = $conn->executeQuery($sql)->fetchAssociative();
        return (float) $result['total'];
    }

    /**
     * Compter commandes annulées du jour
     */
    public function countCommandesAnnuleesJour(): int
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "
            SELECT COUNT(*) as total
            FROM commande
            WHERE DATE(date_commande) = CURRENT_DATE
            AND etat = 'annulee'
        ";
        $result = $conn->executeQuery($sql)->fetchAssociative();
        return (int) $result['total'];
    }

    /**
     * Top 5 burgers les plus vendus du jour
     */
    public function getTopBurgersJour(int $limit = 5): array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "
            SELECT 
                b.nom,
                SUM(lc.quantite) as quantite
            FROM ligne_commande lc
            INNER JOIN commande c ON c.id = lc.commande_id
            LEFT JOIN burger b ON b.id = lc.burger_id
            WHERE DATE(c.date_commande) = CURRENT_DATE
            AND b.id IS NOT NULL
            GROUP BY b.id, b.nom
            ORDER BY quantite DESC
            LIMIT :limit
        ";
        return $conn->executeQuery($sql, ['limit' => $limit])->fetchAllAssociative();
    }

    /**
     * Top 5 menus les plus vendus du jour
     */
    public function getTopMenusJour(int $limit = 5): array
    {
        $conn = $this->getEntityManager()->getConnection();
        $sql = "
            SELECT 
                m.nom,
                SUM(lc.quantite) as quantite
            FROM ligne_commande lc
            INNER JOIN commande c ON c.id = lc.commande_id
            LEFT JOIN menu m ON m.id = lc.menu_id
            WHERE DATE(c.date_commande) = CURRENT_DATE
            AND m.id IS NOT NULL
            GROUP BY m.id, m.nom
            ORDER BY quantite DESC
            LIMIT :limit
        ";
        return $conn->executeQuery($sql, ['limit' => $limit])->fetchAllAssociative();
    }
}
