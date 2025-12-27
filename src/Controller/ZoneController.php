<?php

namespace App\Controller;

use App\Service\ZoneService;
use App\Service\LivreurService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/zones')]
class ZoneController extends AbstractController
{
    public function __construct(
        private ZoneService $zoneService,
        private LivreurService $livreurService
    ) {}

    #[Route('/regroupement', name: 'zone_regroupement')]
    public function regroupement(): Response
    {
        $groupes = $this->zoneService->getCommandesParZone();
        $livreurs = $this->livreurService->listerLivreurs();
        
        return $this->render('zone/regroupement.html.twig', [
            'groupes' => $groupes,
            'livreurs' => $livreurs
        ]);
    }

    #[Route('/{zoneId}/affecter/{livreurId}', name: 'zone_affecter_livreur', methods: ['POST'])]
    public function affecterLivreur(int $zoneId, int $livreurId): Response
    {
        $nombreAffecte = $this->zoneService->affecterLivreur($zoneId, $livreurId);
        
        $this->addFlash('success', "$nombreAffecte commande(s) affectée(s) au livreur");
        
        return $this->redirectToRoute('zone_regroupement');
    }
}
