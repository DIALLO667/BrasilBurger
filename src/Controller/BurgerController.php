<?php

namespace App\Controller;

use App\Repository\BurgerRepository;
use App\Service\FileUploadService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/burgers')]
class BurgerController extends AbstractController
{
    public function __construct(
        private BurgerRepository $burgerRepository,
        private FileUploadService $fileUploadService
    ) {}

    #[Route('', name: 'burger_index')]
    public function index(): Response
    {
        $burgers = $this->burgerRepository->findAll();
        
        return $this->render('burger/index.html.twig', [
            'burgers' => $burgers
        ]);
    }

    #[Route('/nouveau', name: 'burger_create')]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'nom' => $request->request->get('nom'),
                'prix' => $request->request->get('prix')
            ];
            
            // Upload image
            $imageFile = $request->files->get('image');
            if ($imageFile) {
                $data['image'] = $this->fileUploadService->upload($imageFile);
            }
            
            $this->burgerRepository->create($data);
            
            $this->addFlash('success', 'Burger créé avec succès');
            return $this->redirectToRoute('burger_index');
        }
        
        return $this->render('burger/create.html.twig');
    }

    #[Route('/{id}/modifier', name: 'burger_edit')]
    public function edit(int $id, Request $request): Response
    {
        $burger = $this->burgerRepository->findById($id);
        
        if (!$burger) {
            $this->addFlash('error', 'Burger introuvable');
            return $this->redirectToRoute('burger_index');
        }
        
        if ($request->isMethod('POST')) {
            $data = [
                'nom' => $request->request->get('nom'),
                'prix' => $request->request->get('prix')
            ];
            
            $imageFile = $request->files->get('image');
            if ($imageFile) {
                // Supprimer ancienne image
                $this->fileUploadService->delete($burger['image']);
                $data['image'] = $this->fileUploadService->upload($imageFile);
            }
            
            $this->burgerRepository->update($id, $data);
            
            $this->addFlash('success', 'Burger modifié avec succès');
            return $this->redirectToRoute('burger_index');
        }
        
        return $this->render('burger/edit.html.twig', [
            'burger' => $burger
        ]);
    }

    #[Route('/{id}/archiver', name: 'burger_archive', methods: ['POST'])]
    public function archive(int $id): Response
    {
        if ($this->burgerRepository->archive($id)) {
            $this->addFlash('success', 'Burger archivé');
        } else {
            $this->addFlash('error', 'Erreur lors de l\'archivage');
        }
        
        return $this->redirectToRoute('burger_index');
    }
}
