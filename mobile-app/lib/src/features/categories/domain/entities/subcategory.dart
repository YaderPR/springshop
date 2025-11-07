// --- Entidad Mock ---
class Subcategory {
  final String id;
  final String name;
  final String imageUrl;
  final List<int> productIdsMock; // IDs mock para la navegación

  const Subcategory({
    required this.id,
    required this.name, 
    required this.imageUrl,
    required this.productIdsMock,
  });
}