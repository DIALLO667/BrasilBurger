<?php

namespace App\Controller;

use App\Service\LivreurService;
use App\Service\ZoneService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/livreurs')]
class LivreurController extends AbstractController
{
    public function __construct(
        private LivreurService $livreurService,
        private ZoneService $zoneService
    ) {}

    #[Route('', name: 'livreur_index')]
    public function index(): Response
    {
        $livreurs = $this->livreurService->listerLivreurs();
        
        return $this->render('livreur/index.html.twig', [
            'livreurs' => $livreurs
        ]);
    }

    #[Route('/nouveau', name: 'livreur_create')]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'nom' => $request->request->get('nom'),
                'prenom' => $request->request->get('prenom'),
                'telephone' => $request->request->get('telephone'),
                'zone_id' => $request->request->get('zone_id') ?: null
            ];
            
            $this->livreurService->creerLivreur($data);
            
            $this->addFlash('success', 'Livreur créé avec succès');
            return $this->redirectToRoute('livreur_index');
        }
        
        $zones = $this->zoneService->listerZones();
        
        return $this->render('livreur/create.html.twig', [
            'zones' => $zones
        ]);
    }

    #[Route('/{id}/modifier', name: 'livreur_edit')]
    public function edit(int $id, Request $request): Response
    {
        $livreur = $this->livreurService->getLivreur($id);
        
        if (!$livreur) {
            $this->addFlash('error', 'Livreur introuvable');
            return $this->redirectToRoute('livreur_index');
        }
        
        if ($request->isMethod('POST')) {
            $data = [
                'nom' => $request->request->get('nom'),
                'prenom' => $request->request->get('prenom'),
                'telephone' => $request->request->get('telephone'),
                'zone_id' => $request->request->get('zone_id') ?: null
            ];
            
            $this->livreurService->modifierLivreur($id, $data);
            
            $this->addFlash('success', 'Livreur modifié avec succès');
            return $this->redirectToRoute('livreur_index');
        }
        
        $zones = $this->zoneService->listerZones();
        
        return $this->render('livreur/edit.html.twig', [
            'livreur' => $livreur,
            'zones' => $zones
        ]);
    }

    #[Route('/{id}/archiver', name: 'livreur_archive', methods: ['POST'])]
    public function archive(int $id): Response
    {
        if ($this->livreurService->archiverLivreur($id)) {
            $this->addFlash('success', 'Livreur archivé');
        } else {
            $this->addFlash('error', 'Erreur lors de l\'archivage');
        }
        
        return $this->redirectToRoute('livreur_index');
    }
}
