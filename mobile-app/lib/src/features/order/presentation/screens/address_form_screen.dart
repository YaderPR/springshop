import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:springshop/src/core/auth/auth_state_notifier.dart';
import 'package:springshop/src/features/order/domain/entities/address_entity.dart';
import 'package:springshop/src/features/order/domain/services/address_service.dart';
import 'package:springshop/src/features/order/presentation/screens/order_summary_screen.dart';

class AddressFormScreen extends StatefulWidget {
  const AddressFormScreen({super.key});

  @override
  State<AddressFormScreen> createState() => _AddressFormScreenState();
}

class _AddressFormScreenState extends State<AddressFormScreen> {
  final _formKey = GlobalKey<FormState>();
  String _street = '';
  String _city = '';
  String _state = '';
  String _country = '';
  String _zipCode = '';
  String _phoneNumber = '';
  bool _isLoading = false;

  Future<void> _submitForm(BuildContext context) async {
    if (!_formKey.currentState!.validate()) {
      return;
    }
    _formKey.currentState!.save();

    setState(() {
      _isLoading = true;
    });

    final authNotifier = Provider.of<AuthStateNotifier>(context, listen: false);
    final addressService = Provider.of<AddressService>(context, listen: false);

    // Asumiendo que el usuario está logeado y su ID es un entero
    final userId = int.tryParse(authNotifier.user?.id ?? '0') ?? 0;

    if (userId == 0) {
      // Manejar el caso de usuario no válido si fuera necesario
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Error: Usuario no identificado.')),
      );
      setState(() {
        _isLoading = false;
      });
      return;
    }

    final newAddress = AddressEntity(
      id: 0, // El ID se genera en la API
      street: _street,
      city: _city,
      state: _state,
      country: _country,
      zipCode: _zipCode,
      phoneNumber: _phoneNumber.isNotEmpty ? _phoneNumber : null,
      userId: userId,
    );

    try {
      final registeredAddress = await addressService.registerNewAddress(
        newAddress,
      );

      // Si el registro es exitoso, navegar a OrderSummaryScreen
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Dirección guardada exitosamente.')),
      );

      Navigator.of(context).pushReplacement(
        MaterialPageRoute(
          builder: (context) => OrderSummaryScreen(
            userId: userId,
            addressId: registeredAddress
                .id!, // Usamos el ID de la dirección recién registrada
          ),
        ),
      );
    } catch (e) {
      print('Error al registrar la dirección: $e');
      if (!mounted) return;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Fallo al guardar la dirección: $e')),
      );
    } finally {
      if (mounted) {
        setState(() {
          _isLoading = false;
        });
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Ingresar Dirección de Envío'),
        backgroundColor: Theme.of(context).colorScheme.primary,
        foregroundColor: Theme.of(context).colorScheme.onPrimary,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(24.0),
        child: Form(
          key: _formKey,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: <Widget>[
              const Text(
                'Necesitamos tu dirección para calcular el envío.',
                style: TextStyle(fontSize: 16, fontWeight: FontWeight.w500),
              ),
              const SizedBox(height: 20),
              _buildTextFormField(
                label: 'Calle y Número',
                onSaved: (value) => _street = value!,
                validator: (value) =>
                    value!.isEmpty ? 'Por favor ingresa la calle.' : null,
              ),
              _buildTextFormField(
                label: 'Ciudad',
                onSaved: (value) => _city = value!,
                validator: (value) =>
                    value!.isEmpty ? 'Por favor ingresa la ciudad.' : null,
              ),
              Row(
                children: [
                  Expanded(
                    child: _buildTextFormField(
                      label: 'Estado/Provincia',
                      onSaved: (value) => _state = value!,
                      validator: (value) =>
                          value!.isEmpty ? 'Campo requerido.' : null,
                    ),
                  ),
                  const SizedBox(width: 16),
                  Expanded(
                    child: _buildTextFormField(
                      label: 'Código Postal',
                      onSaved: (value) => _zipCode = value!,
                      validator: (value) =>
                          value!.isEmpty ? 'Campo requerido.' : null,
                    ),
                  ),
                ],
              ),
              _buildTextFormField(
                label: 'País',
                onSaved: (value) => _country = value!,
                validator: (value) =>
                    value!.isEmpty ? 'Por favor ingresa el país.' : null,
              ),
              _buildTextFormField(
                label: 'Teléfono (Opcional)',
                keyboardType: TextInputType.phone,
                onSaved: (value) => _phoneNumber = value ?? '',
              ),
              const SizedBox(height: 30),
              _isLoading
                  ? const Center(child: CircularProgressIndicator())
                  : ElevatedButton(
                      onPressed: () => _submitForm(context),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Theme.of(
                          context,
                        ).colorScheme.secondary,
                        foregroundColor: Theme.of(
                          context,
                        ).colorScheme.onSecondary,
                        padding: const EdgeInsets.symmetric(vertical: 16),
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                      ),
                      child: const Text(
                        'Guardar Dirección y Continuar',
                        style: TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                    ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildTextFormField({
    required String label,
    required FormFieldSetter<String> onSaved,
    FormFieldValidator<String>? validator,
    TextInputType keyboardType = TextInputType.text,
  }) {
    return Padding(
      padding: const EdgeInsets.only(bottom: 12.0),
      child: TextFormField(
        decoration: InputDecoration(
          labelText: label,
          border: OutlineInputBorder(borderRadius: BorderRadius.circular(8)),
          contentPadding: const EdgeInsets.symmetric(
            horizontal: 16,
            vertical: 12,
          ),
        ),
        keyboardType: keyboardType,
        onSaved: onSaved,
        validator: validator,
      ),
    );
  }
}
