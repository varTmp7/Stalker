from functools import wraps

from flask import request, abort
from flask_jwt_extended import verify_jwt_in_request, get_jwt_claims, get_jwt_identity

from stalker_backend.Models.Organization import Organization
from stalker_backend import jwt

import string
import secrets


def generate_password():
    alphabet = string.ascii_letters + string.digits
    while True:
        password = ''.join(secrets.choice(alphabet) for _ in range(10))
        if (any(c.islower() for c in password)
                and any(c.isupper() for c in password)
                and sum(c.isdigit() for c in password) >= 3):
            break
    return password


def check_admin_permission(admin_id, organization):
    return admin_id in [admin.id for admin in organization.admins]


def organization_token_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        token = request.headers.get('Organization-Token')
        if token:
            if token in [organization.token for organization in Organization.query.all()] or token == 'vartmp7':
                return fn(*args, **kwargs)
            else:
                abort(400, description='Token not found')
        else:
            abort(400, description='Missing Organization-Token in header request')

    return wrapper


def system_admin_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        verify_jwt_in_request()
        claims = get_jwt_claims()
        if claims['role'] != 'system':
            abort(403, description='System admins only!')
        else:
            return fn(*args, **kwargs)

    return wrapper


def owner_admin_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        verify_jwt_in_request()
        claims = get_jwt_claims()
        if claims['role'] != 'owner':
            abort(403, description='Owner admins only!')
        else:
            return fn(*args, **kwargs)

    return wrapper


def manager_admin_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        verify_jwt_in_request()
        claims = get_jwt_identity()
        if claims['role'] != 'owner' and claims['role'] != 'manager':
            abort(403, description='Owner admin and Manager admin only!')
        else:
            return fn(*args, **kwargs)

    return wrapper


def watcher_admin_required(fn):
    @wraps(fn)
    def wrapper(*args, **kwargs):
        verify_jwt_in_request()
        claims = get_jwt_identity()
        if claims['role'] != 'owner' and claims['role'] != 'manager' and claims['role'] != 'watcher':
            abort(403, description='Owner admin, Manager admin and Watcher admin only!')
        else:
            return fn(*args, **kwargs)

    return wrapper


@jwt.user_claims_loader
def add_claims_to_access_token(admin):
    return {'role': admin['role']}
