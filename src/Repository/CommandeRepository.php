<?php

namespace App\Repository;

use App\Entity\Commande;
use App\DTO\Request\FiltreCommandeDTO;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

class CommandeRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Commande::class);
    }

    /**
     * Liste des commandes avec filtres
     */
    public function findWithFilters(FiltreCommandeDTO $filtres): array
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "
            SELECT 
                c.id,
                c.date_commande,
                c.montant_total,
                c.type_recuperation,
                c.etat,
                c.adresse_livraison,
                cl.nom || ' ' || cl.prenom as client_nom
            FROM commande c
            INNER JOIN client cl ON cl.id = c.client_id
            WHERE 1=1
        ";
        
        $params = [];
        
        if ($filtres->etat) {
            $sql .= " AND c.etat = :etat";
            $params['etat'] = $filtres->etat;
        }
        
        if ($filtres->type) {
            $sql .= " AND c.type_recuperation = :type";
            $params['type'] = $filtres->type;
        }
        
        if ($filtres->dateDebut) {
            $sql .= " AND DATE(c.date_commande) >= :dateDebut";
            $params['dateDebut'] = $filtres->dateDebut->format('Y-m-d');
        }
        
        if ($filtres->dateFin) {
            $sql .= " AND DATE(c.date_commande) <= :dateFin";
            $params['dateFin'] = $filtres->dateFin->format('Y-m-d');
        }
        
        if ($filtres->clientNom) {
            $sql .= " AND (cl.nom ILIKE :clientNom OR cl.prenom ILIKE :clientNom)";
            $params['clientNom'] = '%' . $filtres->clientNom . '%';
        }
        
        $sql .= " ORDER BY c.date_commande DESC";
        
        return $conn->executeQuery($sql, $params)->fetchAllAssociative();
    }

    /**
     * Détail complet d'une commande
     */
    public function getDetailCommande(int $commandeId): ?array
    {
        $conn = $this->getEntityManager()->getConnection();
        
        // 1. Commande
        $sqlCommande = "SELECT * FROM commande WHERE id = :id";
        $commande = $conn->executeQuery($sqlCommande, ['id' => $commandeId])->fetchAssociative();
        
        if (!$commande) {
            return null;
        }
        
        // 2. Client
        $sqlClient = "SELECT * FROM client WHERE id = :id";
        $client = $conn->executeQuery($sqlClient, ['id' => $commande['client_id']])->fetchAssociative();
        
        // 3. Lignes commande
        $sqlLignes = "
            SELECT 
                lc.*,
                COALESCE(b.nom, m.nom) as produit_nom,
                CASE 
                    WHEN b.id IS NOT NULL THEN 'Burger'
                    ELSE 'Menu'
                END as type_produit
            FROM ligne_commande lc
            LEFT JOIN burger b ON b.id = lc.burger_id
            LEFT JOIN menu m ON m.id = lc.menu_id
            WHERE lc.commande_id = :commandeId
        ";
        $lignes = $conn->executeQuery($sqlLignes, ['commandeId' => $commandeId])->fetchAllAssociative();
        
        // 4. Paiement
        $sqlPaiement = "SELECT * FROM paiement WHERE commande_id = :commandeId";
        $paiement = $conn->executeQuery($sqlPaiement, ['commandeId' => $commandeId])->fetchAssociative();
        
        // 5. Livreur (si affecté)
        $livreur = null;
        if ($commande['livreur_id']) {
            $sqlLivreur = "SELECT * FROM livreur WHERE id = :id";
            $livreur = $conn->executeQuery($sqlLivreur, ['id' => $commande['livreur_id']])->fetchAssociative();
        }
        
        // 6. Zone (si livraison)
        $zone = null;
        if ($commande['zone_id']) {
            $sqlZone = "SELECT * FROM zone WHERE id = :id";
            $zone = $conn->executeQuery($sqlZone, ['id' => $commande['zone_id']])->fetchAssociative();
        }
        
        return [
            'commande' => $commande,
            'client' => $client,
            'lignes' => $lignes,
            'paiement' => $paiement ?: null,
            'livreur' => $livreur,
            'zone' => $zone
        ];
    }

    /**
     * Changer l'état d'une commande
     */
    public function changerEtat(int $commandeId, string $nouvelEtat): bool
    {
        $conn = $this->getEntityManager()->getConnection();
        
        $sql = "UPDATE commande SET etat = :etat WHERE id = :id";
        
        return $conn->executeStatement($sql, [
            'etat' => $nouvelEtat,
            'id' => $commandeId
        ]) > 0;
    }

    /**
     * Annuler une commande
     */
    public function annulerCommande(int $commandeId): bool
    {
        return $this->changerEtat($commandeId, 'annulee');
    }

    // ===== MÉTHODES STATISTIQUES (déjà présentes) =====
    
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
