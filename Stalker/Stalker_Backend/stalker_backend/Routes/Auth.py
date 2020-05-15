import hashlib

from flask import Blueprint, jsonify, request
from flask_restful import abort
from flask_jwt_extended import create_access_token, jwt_required, get_jwt_identity

from ..Models.Admin import Admin
from ..Models.Organization import Organization
from ..Utils.AuthUtils import check_admin_permission, generate_password
from stalker_backend.utils import send_email_reset_password
from stalker_backend import db_alchemy

auth_routes = Blueprint('auth', __name__, url_prefix='/')


@auth_routes.route('login', methods=['POST'])
def login():
    email = request.form['email']
    # TODO: password = request.form['password']
    admin = Admin.query.filter(Admin.email == email).first_or_404(description='Username or password incorrect')
    # TODO check password
    admin_info = admin.to_dict(None)
    access_token = create_access_token(admin_info)
    admin_access_info = {**{'access_token': access_token}, **admin_info}
    return jsonify(admin_access_info), 200


@auth_routes.route('reset-password', methods=['POST'])
def reset_password():
    email = request.form['email']

    admin = Admin.query.filter_by(email=email).first_or_404(description='Email not found')
    new_password = generate_password()
    hashed_password = hashlib.sha512(new_password.encode('utf-8')).hexdigest()
    admin.password = hashed_password
    db_alchemy.session.commit()

    send_email_reset_password(email, new_password, admin.name, admin.surname)
    return jsonify({'status': 'changed'}), 200


@auth_routes.route('/select-organization', methods=['GET'])
@jwt_required
def select_organization():
    organization_id = request.args.get('organization_id')
    if organization_id:
        admin = get_jwt_identity()
        if check_admin_permission(admin['id'], Organization.query.get(organization_id)):
            new_admin_info = Admin.query.get(admin['id']).to_dict(organization_id)
            access_token = create_access_token(new_admin_info)
            admin_access_info = {**{'access_token': access_token}, **new_admin_info}
            return jsonify(admin_access_info), 200
        else:
            abort(403)
    else:
        abort(400)
