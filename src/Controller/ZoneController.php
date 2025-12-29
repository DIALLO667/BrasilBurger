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

    #[Route('', name: 'zone_index')]
    public function index(): Response
    {
        $zones = $this->zoneService->listerZones();
        
        return $this->render('zone/index.html.twig', [
            'zones' => $zones
        ]);
    }

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

    #[Route('/nouvelle', name: 'zone_create')]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            // Récupérer quartiers (minimum 2)
            $quartiers = array_filter([
                $request->request->get('quartier_1'),
                $request->request->get('quartier_2'),
                $request->request->get('quartier_3'),
                $request->request->get('quartier_4'),
                $request->request->get('quartier_5'),
            ]);
            
            if (count($quartiers) < 2) {
                $this->addFlash('error', 'Veuillez saisir au moins 2 quartiers');
                return $this->redirectToRoute('zone_create');
            }
            
            $data = [
                'nom' => $request->request->get('nom'),
                'quartiers' => $quartiers,
                'prix_livraison' => $request->request->get('prix_livraison')
            ];
            
            $this->zoneService->creerZone($data);
            
            $this->addFlash('success', 'Zone créée avec succès');
            return $this->redirectToRoute('zone_index');
        }
        
        return $this->render('zone/create.html.twig');
    }

    #[Route('/{id}/modifier', name: 'zone_edit')]
    public function edit(int $id, Request $request): Response
    {
        $zone = $this->zoneService->getZone($id);
        
        if (!$zone) {
            $this->addFlash('error', 'Zone introuvable');
            return $this->redirectToRoute('zone_index');
        }
        
        // Décoder quartiers JSON
        $quartiers = json_decode($zone['quartiers'], true);
        
        if ($request->isMethod('POST')) {
            $quartiersInput = array_filter([
                $request->request->get('quartier_1'),
                $request->request->get('quartier_2'),
                $request->request->get('quartier_3'),
                $request->request->get('quartier_4'),
                $request->request->get('quartier_5'),
            ]);
            
            if (count($quartiersInput) < 2) {
                $this->addFlash('error', 'Veuillez saisir au moins 2 quartiers');
                return $this->redirectToRoute('zone_edit', ['id' => $id]);
            }
            
            $data = [
                'nom' => $request->request->get('nom'),
                'quartiers' => $quartiersInput,
                'prix_livraison' => $request->request->get('prix_livraison')
            ];
            
            $this->zoneService->modifierZone($id, $data);
            
            $this->addFlash('success', 'Zone modifiée avec succès');
            return $this->redirectToRoute('zone_index');
        }
        
        return $this->render('zone/edit.html.twig', [
            'zone' => $zone,
            'quartiers' => $quartiers
        ]);
    }

    #[Route('/{id}/supprimer', name: 'zone_delete', methods: ['POST'])]
    public function delete(int $id): Response
    {
        if ($this->zoneService->supprimerZone($id)) {
            $this->addFlash('success', 'Zone supprimée');
        } else {
            $this->addFlash('error', 'Erreur lors de la suppression');
        }
        
        return $this->redirectToRoute('zone_index');
    }

    #[Route('/{zoneId}/affecter/{livreurId}', name: 'zone_affecter_livreur', methods: ['POST'])]
    public function affecterLivreur(int $zoneId, int $livreurId): Response
    {
        $nombreAffecte = $this->zoneService->affecterLivreur($zoneId, $livreurId);
        
        $this->addFlash('success', "$nombreAffecte commande(s) affectée(s) au livreur");
        
        return $this->redirectToRoute('zone_regroupement');
    }
}
