import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:url_launcher/url_launcher.dart';

import 'package:springshop/src/features/cart/domain/entities/cart_item.dart';
import 'package:springshop/src/features/order/domain/services/order_service.dart';

// Importamos OrderSummaryCalculated y otras pantallas
import '../screens/order_summary_screen.dart';

// ====================================================================
//  WIDGETS MODULARES
// ====================================================================

/// Título de sección reusable.
class SectionTitle extends StatelessWidget {
  final String title;

  const SectionTitle({super.key, required this.title});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Text(
        title,
        style: theme.textTheme.titleLarge!.copyWith(
          fontWeight: FontWeight.bold,
          color: theme.colorScheme.onSurface,
        ),
      ),
    );
  }
}

/// Muestra los IDs de la orden (para debug).
class OrderIdsDebugInfo extends StatelessWidget {
  final int cartId;
  final int userId;
  final int addressId;

  const OrderIdsDebugInfo({
    super.key,
    required this.cartId,
    required this.userId,
    required this.addressId,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: theme.colorScheme.secondaryContainer,
        borderRadius: BorderRadius.circular(10),
        border: Border.all(color: theme.colorScheme.outlineVariant),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            'IDs para la Solicitud de Pago',
            style: theme.textTheme.bodyMedium!.copyWith(
              fontWeight: FontWeight.w600,
              color: theme.colorScheme.onSecondaryContainer,
            ),
          ),
          const Divider(height: 10),
          Text('Cart ID: $cartId'),
          Text('User ID: $userId'),
          Text('Address ID: $addressId'),
        ],
      ),
    );
  }
}

/// Muestra información de la dirección de envío.
class ShippingAddressCard extends StatelessWidget {
  final String addressLine1;
  final String? username;
  final String city;
  final String zipCode;
  final String? phoneNumber;

  const ShippingAddressCard({
    super.key,
    this.username,
    required this.addressLine1,
    required this.city,
    required this.zipCode,
    this.phoneNumber,
  });

  Widget _buildInfoRow(BuildContext context, IconData icon, String text) {
    final theme = Theme.of(context);

    return Padding(
      padding: const EdgeInsets.only(bottom: 4.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Icon(icon, size: 20, color: theme.colorScheme.primary),
          const SizedBox(width: 12),
          Expanded(
            child: Text(
              text,
              style: theme.textTheme.bodyLarge,
            ),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              username != null ? 'Destinatario ($username)' : 'Destinatario',
              style: theme.textTheme.titleMedium!.copyWith(
                fontWeight: FontWeight.w600,
              ),
            ),
            const SizedBox(height: 8),

            _buildInfoRow(context, Icons.home_outlined, addressLine1),
            _buildInfoRow(context, Icons.location_city_outlined, '$city, $zipCode'),

            if (phoneNumber != null && phoneNumber!.isNotEmpty)
              _buildInfoRow(context, Icons.phone_outlined, phoneNumber!),

            const SizedBox(height: 8),
            TextButton.icon(
              onPressed: () {
                debugPrint('Navegar a la selección/edición de dirección...');
              },
              icon: Icon(Icons.edit, size: 18, color: theme.colorScheme.secondary),
              label: Text(
                'Cambiar',
                style: TextStyle(color: theme.colorScheme.secondary),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

/// Lista de productos en el pedido.
class OrderItemsList extends StatelessWidget {
  final List<CartItem> items;

  const OrderItemsList({super.key, required this.items});

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Column(
      children: items.map((item) {
        final product = item.productDetails;

        if (product == null) {
          return const Text('Error: Producto no disponible');
        }

        return Padding(
          padding: const EdgeInsets.symmetric(vertical: 8.0),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Container(
                width: 50,
                height: 50,
                decoration: BoxDecoration(
                  color: theme.colorScheme.surfaceVariant,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Icon(Icons.shopping_bag_outlined, color: theme.colorScheme.primary),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      product.name,
                      style: theme.textTheme.titleSmall!.copyWith(
                        fontWeight: FontWeight.w500,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                    Text(
                      'Cantidad: ${item.quantity}',
                      style: theme.textTheme.bodySmall!.copyWith(
                        color: theme.colorScheme.outline,
                      ),
                    ),
                  ],
                ),
              ),
              Text(
                ' \$${item.subtotal.toStringAsFixed(2)}',
                style: theme.textTheme.titleMedium!.copyWith(
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }
}

/// Fila de resumen de precios.
class _PriceRow extends StatelessWidget {
  final String label;
  final double amount;
  final bool isTotal;

  const _PriceRow({
    required this.label,
    required this.amount,
    this.isTotal = false,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            style: theme.textTheme.bodyLarge!.copyWith(
              fontSize: isTotal ? 18 : 15,
              fontWeight: isTotal ? FontWeight.bold : FontWeight.normal,
              color: isTotal
                  ? theme.colorScheme.onSurface
                  : theme.colorScheme.onSurfaceVariant,
            ),
          ),
          Text(
            '\$${amount.toStringAsFixed(2)}',
            style: theme.textTheme.bodyLarge!.copyWith(
              fontSize: isTotal ? 18 : 15,
              fontWeight: isTotal ? FontWeight.bold : FontWeight.w500,
              color: isTotal ? theme.colorScheme.primary : theme.colorScheme.onSurface,
            ),
          ),
        ],
      ),
    );
  }
}

/// Muestra el resumen del precio y total.
class PriceSummaryCard extends StatelessWidget {
  final OrderSummaryCalculated summary;

  const PriceSummaryCard({super.key, required this.summary});

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 1,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            _PriceRow(label: 'Subtotal', amount: summary.subtotal),
            _PriceRow(label: 'Costo de Envío', amount: summary.shippingCost),
            _PriceRow(
              label: 'Impuestos (${(summary.taxRate * 100).toInt()}%)',
              amount: summary.taxes,
            ),
            const Divider(height: 20),
            _PriceRow(label: 'Total a Pagar', amount: summary.total, isTotal: true),
          ],
        ),
      ),
    );
  }
}

/// Botón “Pagar Ahora”.
class PayNowButton extends StatefulWidget {
  final int cartId;
  final int userId;
  final int addressId;

  const PayNowButton({
    super.key,
    required this.cartId,
    required this.userId,
    required this.addressId,
  });

  @override
  State<PayNowButton> createState() => _PayNowButtonState();
}

class _PayNowButtonState extends State<PayNowButton> {
  bool _isLoading = false;

  Future<void> _handleCheckout(BuildContext context) async {
    if (_isLoading) return;

    final navigator = Navigator.of(context);
    final messenger = ScaffoldMessenger.of(context);
    const deepLink = 'springshop://checkout';

    setState(() => _isLoading = true);

    final orderService = context.read<OrderService>();

    try {
      final result = await orderService.processCheckout(
        cartId: widget.cartId,
        userId: widget.userId,
        addressId: widget.addressId,
        redirectUrl: deepLink,
      );

      final checkoutUrl = result['checkoutUrl'];

      if (checkoutUrl is! String || checkoutUrl.isEmpty) {
        messenger.showSnackBar(
          const SnackBar(content: Text('Error: URL inválida del checkout')),
        );
        setState(() => _isLoading = false);
        return;
      }

      final uri = Uri.tryParse(checkoutUrl);

      if (uri == null || !(await canLaunchUrl(uri))) {
        messenger.showSnackBar(
          const SnackBar(content: Text('No se pudo abrir el navegador')),
        );
        setState(() => _isLoading = false);
        return;
      }

      await launchUrl(uri, mode: LaunchMode.externalApplication);

      if (navigator.mounted) {
        navigator.pushReplacementNamed('/payment-wait');
      }
    } catch (e) {
      messenger.showSnackBar(
        SnackBar(content: Text('Error en el checkout: $e')),
      );
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 10.0),
      decoration: BoxDecoration(
        color: theme.colorScheme.surface,
        boxShadow: [
          BoxShadow(
            color: theme.colorScheme.shadow.withOpacity(0.15),
            blurRadius: 5.0,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: ElevatedButton(
        onPressed: _isLoading ? null : () => _handleCheckout(context),
        style: ElevatedButton.styleFrom(
          minimumSize: const Size(double.infinity, 50),
          backgroundColor: theme.colorScheme.primary,
          foregroundColor: theme.colorScheme.onPrimary,
          textStyle: const TextStyle(fontWeight: FontWeight.bold),
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
          elevation: 0,
        ),
        child: _isLoading
            ? const SizedBox(
                width: 24,
                height: 24,
                child: CircularProgressIndicator(
                  color: Colors.white,
                  strokeWidth: 3.0,
                ),
              )
            : const Text('Pagar Ahora', style: TextStyle(fontSize: 18)),
      ),
    );
  }
}
