<?php

namespace App\Controller;

use App\Repository\ComplementRepository;
use App\Service\FileUploadService;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

#[Route('/complements')]
class ComplementController extends AbstractController
{
    public function __construct(
        private ComplementRepository $complementRepository,
        private FileUploadService $fileUploadService
    ) {}

    #[Route('', name: 'complement_index')]
    public function index(): Response
    {
        $complements = $this->complementRepository->findAll();
        
        return $this->render('complement/index.html.twig', [
            'complements' => $complements
        ]);
    }

    #[Route('/nouveau', name: 'complement_create')]
    public function create(Request $request): Response
    {
        if ($request->isMethod('POST')) {
            $data = [
                'nom' => $request->request->get('nom'),
                'prix' => $request->request->get('prix')
            ];
            
            $imageFile = $request->files->get('image');
            if ($imageFile) {
                $data['image'] = $this->fileUploadService->upload($imageFile);
            }
            
            $this->complementRepository->create($data);
            
            $this->addFlash('success', 'Complément créé avec succès');
            return $this->redirectToRoute('complement_index');
        }
        
        return $this->render('complement/create.html.twig');
    }

    #[Route('/{id}/modifier', name: 'complement_edit')]
    public function edit(int $id, Request $request): Response
    {
        $complement = $this->complementRepository->findById($id);
        
        if (!$complement) {
            $this->addFlash('error', 'Complément introuvable');
            return $this->redirectToRoute('complement_index');
        }
        
        if ($request->isMethod('POST')) {
            $data = [
                'nom' => $request->request->get('nom'),
                'prix' => $request->request->get('prix')
            ];
            
            $imageFile = $request->files->get('image');
            if ($imageFile) {
                $this->fileUploadService->delete($complement['image']);
                $data['image'] = $this->fileUploadService->upload($imageFile);
            }
            
            $this->complementRepository->update($id, $data);
            
            $this->addFlash('success', 'Complément modifié avec succès');
            return $this->redirectToRoute('complement_index');
        }
        
        return $this->render('complement/edit.html.twig', [
            'complement' => $complement
        ]);
    }

    #[Route('/{id}/archiver', name: 'complement_archive', methods: ['POST'])]
    public function archive(int $id): Response
    {
        if ($this->complementRepository->archive($id)) {
            $this->addFlash('success', 'Complément archivé');
        } else {
            $this->addFlash('error', 'Erreur lors de l\'archivage');
        }
        
        return $this->redirectToRoute('complement_index');
    }
}
